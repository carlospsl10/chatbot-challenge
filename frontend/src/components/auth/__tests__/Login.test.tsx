import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import Login from '../Login';

// Mock the AuthContext
const mockLogin = jest.fn();
jest.mock('../../../contexts/AuthContext', () => ({
  useAuth: () => ({
    login: mockLogin,
    isAuthenticated: false,
    loading: false,
  }),
}));

const theme = createTheme();

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        {component}
      </BrowserRouter>
    </ThemeProvider>
  );
};

describe('Login Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders login form with all fields', () => {
    renderWithProviders(<Login />);
    
    expect(screen.getByLabelText(/email address/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
    expect(screen.getByText(/don't have an account\? sign up/i)).toBeInTheDocument();
  });

  test('shows demo credentials info', () => {
    renderWithProviders(<Login />);
    
    expect(screen.getByText(/demo credentials/i)).toBeInTheDocument();
    expect(screen.getByText(/email: john\.doe@example\.com/i)).toBeInTheDocument();
    expect(screen.getByText(/password: password123/i)).toBeInTheDocument();
  });

  test('handles successful login', async () => {
    mockLogin.mockResolvedValue(undefined);

    renderWithProviders(<Login />);
    
    const emailInput = screen.getByLabelText(/email address/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));
    
    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('john.doe@example.com', 'password123');
    });
  });

  test('handles login error', async () => {
    const errorMessage = 'Invalid email or password';
    mockLogin.mockRejectedValue(new Error(errorMessage));

    renderWithProviders(<Login />);
    
    const emailInput = screen.getByLabelText(/email address/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
    
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));
    
    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });

  test('toggles password visibility', () => {
    renderWithProviders(<Login />);
    
    const passwordInput = screen.getByLabelText(/password/i);
    const allButtons = screen.getAllByRole('button');
    const toggleButton = allButtons.find(button => button.textContent !== 'Sign In');
    
    // Initially password should be hidden
    expect(passwordInput).toHaveAttribute('type', 'password');
    
    // Click toggle button
    fireEvent.click(toggleButton!);
    
    // Password should be visible
    expect(passwordInput).toHaveAttribute('type', 'text');
    
    // Click toggle button again
    fireEvent.click(toggleButton!);
    
    // Password should be hidden again
    expect(passwordInput).toHaveAttribute('type', 'password');
  });

  test('shows loading state during login', async () => {
    mockLogin.mockImplementation(() => 
      new Promise(resolve => setTimeout(resolve, 100))
    );

    renderWithProviders(<Login />);
    
    const emailInput = screen.getByLabelText(/email address/i);
    const passwordInput = screen.getByLabelText(/password/i);
    
    fireEvent.change(emailInput, { target: { value: 'john.doe@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));
    
    // Should show loading state
    expect(screen.getByText(/signing in\.\.\./i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /signing in\.\.\./i })).toBeDisabled();
  });
}); 
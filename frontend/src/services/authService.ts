import axios from 'axios';
import environment from '../config/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  customerId: number;
  email: string;
  firstName: string;
  lastName: string;
  expiresIn: number; // This is a timestamp in milliseconds
}

export interface AuthError {
  error: string;
  timestamp: string;
  status: number;
}

class AuthService {
  private tokenKey = 'chatbot_token';
  private userKey = 'chatbot_user';

  // Set up axios defaults
  constructor() {
    axios.defaults.baseURL = environment.getApiUrl();
    axios.defaults.headers.common['Content-Type'] = 'application/json';
    
    // Log configuration in debug mode
    environment.logConfig();
  }

  /**
   * Register a new customer
   */
  async register(email: string, password: string, firstName: string, lastName: string): Promise<LoginResponse> {
    try {
      const response = await axios.post<LoginResponse>('/api/auth/register', {
        email,
        password,
        firstName,
        lastName
      });

      // Store token and user info
      this.setToken(response.data.token);
      this.setUser(response.data);

      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        throw new Error(error.response.data.error || 'Registration failed');
      }
      throw new Error('Network error. Please check your connection.');
    }
  }

  /**
   * Authenticate user with email and password
   */
  async login(email: string, password: string): Promise<LoginResponse> {
    try {
      const response = await axios.post<LoginResponse>('/api/auth/login', {
        email,
        password
      });

      // Store token and user info
      this.setToken(response.data.token);
      this.setUser(response.data);

      return response.data;
    } catch (error: any) {
      if (error.response?.data) {
        throw new Error(error.response.data.error || 'Authentication failed');
      }
      throw new Error('Network error. Please check your connection.');
    }
  }

  /**
   * Logout user and clear stored data
   */
  async logout(): Promise<void> {
    try {
      const token = this.getToken();
      if (token) {
        await axios.post('/api/auth/logout', {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      this.clearAuth();
    }
  }

  /**
   * Get current user information
   */
  getUser(): LoginResponse | null {
    try {
      const userStr = localStorage.getItem(this.userKey);
      return userStr ? JSON.parse(userStr) : null;
    } catch (error) {
      console.error('Error parsing user data:', error);
      return null;
    }
  }

  /**
   * Get stored token
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Set authentication token
   */
  private setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  /**
   * Set user information
   */
  private setUser(user: LoginResponse): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  /**
   * Clear all authentication data
   */
  private clearAuth(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    delete axios.defaults.headers.common['Authorization'];
  }

  /**
   * Initialize axios with stored token
   */
  initializeAuth(): void {
    const token = this.getToken();
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
  }
}

export default new AuthService(); 
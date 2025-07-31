import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Container,
  Avatar,
  Menu,
  MenuItem,
  IconButton,
  Tabs,
  Tab
} from '@mui/material';
import { 
  Logout as LogoutIcon, 
  AccountCircle,
  Chat as ChatIcon,
  History as HistoryIcon
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = async () => {
    setIsLoggingOut(true);
    handleMenuClose();

    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout error:', error);
      // Force logout even if API call fails
      navigate('/login');
    } finally {
      setIsLoggingOut(false);
    }
  };

  const getUserDisplayName = () => {
    if (user) {
      return `${user.firstName} ${user.lastName}`;
    }
    return 'User';
  };

  const getCurrentTab = () => {
    if (location.pathname === '/chat') return 0;
    if (location.pathname === '/orders') return 1;
    return 0;
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    if (newValue === 0) {
      navigate('/chat');
    } else if (newValue === 1) {
      navigate('/orders');
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Order Status Chatbot
          </Typography>
          
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Tabs 
              value={getCurrentTab()} 
              onChange={handleTabChange}
              sx={{ 
                '& .MuiTab-root': { 
                  color: 'white',
                  minHeight: 'auto',
                  padding: '6px 16px'
                },
                '& .Mui-selected': {
                  color: 'white'
                }
              }}
            >
              <Tab 
                icon={<ChatIcon />} 
                label="Chat" 
                iconPosition="start"
              />
              <Tab 
                icon={<HistoryIcon />} 
                label="Order History" 
                iconPosition="start"
              />
            </Tabs>
            
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' } }}>
                Welcome, {getUserDisplayName()}
              </Typography>
              
              <IconButton
                size="large"
                onClick={handleMenuOpen}
                color="inherit"
                disabled={isLoggingOut}
              >
                <AccountCircle />
              </IconButton>
              
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'right',
                }}
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
              >
                <MenuItem disabled>
                  <Typography variant="body2">
                    {user?.email}
                  </Typography>
                </MenuItem>
                <MenuItem onClick={handleLogout} disabled={isLoggingOut}>
                  <LogoutIcon sx={{ mr: 1 }} />
                  {isLoggingOut ? 'Logging out...' : 'Logout'}
                </MenuItem>
              </Menu>
            </Box>
          </Box>
        </Toolbar>
      </AppBar>
      
      <Box component="main" sx={{ flexGrow: 1, overflow: 'hidden' }}>
        {children}
      </Box>
    </Box>
  );
};

export default Layout; 
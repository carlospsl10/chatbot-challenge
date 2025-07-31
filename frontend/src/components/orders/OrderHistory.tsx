import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Card,
  CardContent,
  Grid,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  Refresh as RefreshIcon,
  Visibility as ViewIcon,
  LocalShipping as ShippingIcon,
  CheckCircle as DeliveredIcon,
  Schedule as ProcessingIcon,
  Cancel as CancelledIcon
} from '@mui/icons-material';
import orderService, { Order } from '../../services/orderService';
import { useAuth } from '../../contexts/AuthContext';

interface OrderHistoryProps {
  onViewOrder?: (orderNumber: string) => void;
}

const OrderHistory: React.FC<OrderHistoryProps> = ({ onViewOrder }) => {
  const { user } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');
  const [filter, setFilter] = useState<string>('all');
  const [limit, setLimit] = useState<number>(10);

  const loadOrders = async () => {
    setLoading(true);
    setError('');
    
    try {
      let ordersData: Order[];
      
      if (filter === 'recent') {
        ordersData = await orderService.getMyRecentOrders();
      } else if (filter === 'all') {
        ordersData = await orderService.getMyOrders(limit);
      } else {
        ordersData = await orderService.getMyOrdersByStatus(filter);
      }
      
      setOrders(ordersData);
    } catch (error: any) {
      setError(error.message || 'Failed to load order history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, [filter, limit]);

  const getStatusIcon = (status: string) => {
    switch (status.toUpperCase()) {
      case 'SHIPPED':
        return <ShippingIcon color="primary" />;
      case 'DELIVERED':
        return <DeliveredIcon color="success" />;
      case 'PROCESSING':
        return <ProcessingIcon color="warning" />;
      case 'CANCELLED':
        return <CancelledIcon color="error" />;
      default:
        return <ProcessingIcon />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'SHIPPED':
        return 'primary';
      case 'DELIVERED':
        return 'success';
      case 'PROCESSING':
        return 'warning';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const handleViewOrder = (orderNumber: string) => {
    if (onViewOrder) {
      onViewOrder(orderNumber);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 2 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h5" component="h2">
            My Order History
          </Typography>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadOrders}
            disabled={loading}
          >
            Refresh
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        <Box display="flex" gap={2} mb={3} alignItems="center">
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Filter</InputLabel>
            <Select
              value={filter}
              label="Filter"
              onChange={(e) => setFilter(e.target.value)}
            >
              <MenuItem value="all">All Orders</MenuItem>
              <MenuItem value="recent">Recent (30 days)</MenuItem>
              <MenuItem value="PROCESSING">Processing</MenuItem>
              <MenuItem value="SHIPPED">Shipped</MenuItem>
              <MenuItem value="DELIVERED">Delivered</MenuItem>
              <MenuItem value="CANCELLED">Cancelled</MenuItem>
            </Select>
          </FormControl>

          {filter === 'all' && (
            <TextField
              label="Limit"
              type="number"
              value={limit}
              onChange={(e) => setLimit(Number(e.target.value))}
              sx={{ width: 100 }}
              inputProps={{ min: 1, max: 50 }}
            />
          )}
        </Box>

        {orders.length === 0 ? (
          <Card>
            <CardContent>
              <Typography variant="h6" color="textSecondary" align="center">
                No orders found
              </Typography>
              <Typography variant="body2" color="textSecondary" align="center">
                {filter === 'recent' 
                  ? "You don't have any recent orders in the last 30 days."
                  : "You don't have any orders yet."
                }
              </Typography>
            </CardContent>
          </Card>
        ) : (
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Order Number</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Total Amount</TableCell>
                  <TableCell>Created Date</TableCell>
                  <TableCell>Shipping Address</TableCell>
                  <TableCell align="center">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {orders.map((order) => (
                  <TableRow key={order.id} hover>
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold">
                        {order.orderNumber}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        icon={getStatusIcon(order.status)}
                        label={order.status}
                        color={getStatusColor(order.status) as any}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold">
                        {formatCurrency(order.totalAmount)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {formatDate(order.createdDate)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" sx={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                        {order.shippingAddress}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <Tooltip title="View Order Details">
                        <IconButton
                          size="small"
                          onClick={() => handleViewOrder(order.orderNumber)}
                          color="primary"
                        >
                          <ViewIcon />
                        </IconButton>
                      </Tooltip>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}

        {orders.length > 0 && (
          <Box mt={2}>
            <Typography variant="body2" color="textSecondary">
              Showing {orders.length} order{orders.length !== 1 ? 's' : ''}
              {filter === 'all' && ` (limited to ${limit})`}
            </Typography>
          </Box>
        )}
      </Paper>
    </Box>
  );
};

export default OrderHistory; 
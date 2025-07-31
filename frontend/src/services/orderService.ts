import axios from 'axios';
import authService from './authService';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export interface Order {
  id: number;
  orderNumber: string;
  customerId: number;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  createdDate: string;
  updatedDate?: string;
}

export interface OrderHistoryResponse {
  orders: Order[];
  totalCount: number;
}

export interface OrderError {
  error: string;
  timestamp: string;
  status: number;
}

class OrderService {
  constructor() {
    axios.defaults.baseURL = API_BASE_URL;
    axios.defaults.headers.common['Content-Type'] = 'application/json';
  }

  /**
   * Get all orders for the authenticated customer
   * @param limit Maximum number of orders to return (default: 10)
   * @returns Promise with order list
   */
  async getMyOrders(limit: number = 10): Promise<Order[]> {
    try {
      const token = authService.getToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const response = await axios.get<Order[]>(`/api/orders/my-orders?limit=${limit}`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        throw new Error('Authentication failed. Please log in again.');
      } else if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Failed to fetch order history. Please try again.');
      }
    }
  }

  /**
   * Get recent orders (last 30 days) for the authenticated customer
   * @returns Promise with recent order list
   */
  async getMyRecentOrders(): Promise<Order[]> {
    try {
      const token = authService.getToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const response = await axios.get<Order[]>('/api/orders/my-orders/recent');
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        throw new Error('Authentication failed. Please log in again.');
      } else if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Failed to fetch recent orders. Please try again.');
      }
    }
  }

  /**
   * Get orders by status for the authenticated customer
   * @param status Order status to filter by
   * @returns Promise with filtered order list
   */
  async getMyOrdersByStatus(status: string): Promise<Order[]> {
    try {
      const token = authService.getToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const response = await axios.get<Order[]>(`/api/orders/my-orders/status/${status}`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        throw new Error('Authentication failed. Please log in again.');
      } else if (error.response?.status === 400) {
        throw new Error('Invalid status provided. Please use: PROCESSING, SHIPPED, DELIVERED, or CANCELLED');
      } else if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Failed to fetch orders by status. Please try again.');
      }
    }
  }

  /**
   * Get specific order by order number
   * @param orderNumber Order number to retrieve
   * @returns Promise with order details
   */
  async getOrderByNumber(orderNumber: string): Promise<Order> {
    try {
      const token = authService.getToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const response = await axios.get<Order>(`/api/orders/${orderNumber}`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        throw new Error('Authentication failed. Please log in again.');
      } else if (error.response?.status === 403) {
        throw new Error('You do not have permission to view this order.');
      } else if (error.response?.status === 404) {
        throw new Error(`Order ${orderNumber} not found.`);
      } else if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Failed to fetch order details. Please try again.');
      }
    }
  }

  /**
   * Get order tracking information
   * @param orderNumber Order number to track
   * @returns Promise with tracking information
   */
  async getOrderTracking(orderNumber: string): Promise<any> {
    try {
      const token = authService.getToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const response = await axios.get(`/api/orders/track/${orderNumber}`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        throw new Error('Authentication failed. Please log in again.');
      } else if (error.response?.status === 403) {
        throw new Error('You do not have permission to track this order.');
      } else if (error.response?.status === 404) {
        throw new Error(`Tracking information for order ${orderNumber} not found.`);
      } else if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Failed to fetch tracking information. Please try again.');
      }
    }
  }

  /**
   * Check service health
   * @returns Promise with health status
   */
  async checkHealth(): Promise<boolean> {
    try {
      const response = await axios.get('/api/orders/my-orders?limit=1');
      return response.status === 200;
    } catch (error) {
      console.error('Order service health check failed:', error);
      return false;
    }
  }
}

export default new OrderService(); 
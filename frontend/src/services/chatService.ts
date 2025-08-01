import axios from 'axios';
import authService from './authService';
import environment from '../config/environment';

export interface ChatRequest {
  message: string;
}

export interface ChatResponse {
  message: string;
  intent: string;
  confidence: number;
  timestamp: string;
}

export interface ChatError {
  error: string;
  timestamp: string;
  status: number;
}

class ChatService {
  constructor() {
    // Set up axios defaults
    axios.defaults.baseURL = environment.getApiUrl();
    axios.defaults.headers.common['Content-Type'] = 'application/json';
  }

  /**
   * Send a message to the chat API
   * @param message User message to send
   * @returns ChatResponse with AI-generated reply
   */
  async sendMessage(message: string): Promise<ChatResponse> {
    try {
      // Get authentication token
      const token = authService.getToken();
      if (!token) {
        throw new Error('Authentication required');
      }

      // Set authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const response = await axios.post<ChatResponse>('/api/chat/message', {
        message
      });

      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        throw new Error('Authentication failed. Please log in again.');
      } else if (error.response?.status === 429) {
        throw new Error('Rate limit exceeded. Please wait a moment before sending another message.');
      } else if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Failed to send message. Please try again.');
      }
    }
  }

  /**
   * Check if chat service is healthy
   * @returns Promise<boolean>
   */
  async checkHealth(): Promise<boolean> {
    try {
      const response = await axios.get('/api/chat/health');
      return response.status === 200;
    } catch (error) {
      console.error('Chat service health check failed:', error);
      return false;
    }
  }
}

export default new ChatService(); 
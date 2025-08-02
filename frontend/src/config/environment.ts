/**
 * Environment Configuration
 * Centralized configuration management for different environments
 */

export interface EnvironmentConfig {
  apiUrl: string;
  environment: string;
  useProxy: boolean;
  debug: boolean;
  logLevel: string;
  enableHotReload: boolean;
  backendUrl: string;
  nginxConfig: string;
  timeout: number;
  retryAttempts: number;
}

class EnvironmentConfiguration {
  private config: EnvironmentConfig;

  constructor() {
    this.config = {
      apiUrl: this.resolveApiUrl(),
      environment: this.resolveEnvironment(),
      useProxy: this.resolveUseProxy(),
      debug: this.resolveDebug(),
      logLevel: this.resolveLogLevel(),
      enableHotReload: this.resolveEnableHotReload(),
      backendUrl: this.resolveBackendUrl(),
      nginxConfig: this.resolveNginxConfig(),
      timeout: 30000,
      retryAttempts: 3
    };
  }

  /**
   * Resolve API URL based on environment
   */
  private resolveApiUrl(): string {
    if (process.env.REACT_APP_API_URL) {
      return process.env.REACT_APP_API_URL;
    }

    switch (process.env.NODE_ENV) {
      case 'development':
        return 'http://localhost:8080';
      case 'production':
        return 'https://chatbot-challenge-production-03c8.up.railway.app';
      default:
        return 'http://localhost:8080';
    }
  }

  /**
   * Resolve environment name
   */
  private resolveEnvironment(): string {
    return process.env.REACT_APP_ENV || process.env.NODE_ENV || 'development';
  }

  /**
   * Resolve if proxy should be used
   */
  private resolveUseProxy(): boolean {
    if (process.env.REACT_APP_USE_PROXY) {
      return process.env.REACT_APP_USE_PROXY === 'true';
    }
    return process.env.NODE_ENV === 'development';
  }

  /**
   * Resolve if debug mode is enabled
   */
  private resolveDebug(): boolean {
    if (process.env.REACT_APP_DEBUG) {
      return process.env.REACT_APP_DEBUG === 'true';
    }
    return process.env.NODE_ENV === 'development';
  }

  /**
   * Resolve log level
   */
  private resolveLogLevel(): string {
    return process.env.REACT_APP_LOG_LEVEL || (this.resolveEnvironment() === "development" ? 'debug' : 'error');
  }

  /**
   * Resolve if hot reload is enabled
   */
  private resolveEnableHotReload(): boolean {
    if (process.env.REACT_APP_ENABLE_HOT_RELOAD) {
      return process.env.REACT_APP_ENABLE_HOT_RELOAD === 'true';
    }
    return this.resolveEnvironment() === "development";
  }

  /**
   * Resolve backend URL
   */
  private resolveBackendUrl(): string {
    return process.env.REACT_APP_BACKEND_URL || this.resolveApiUrl();
  }

  /**
   * Resolve nginx config type
   */
  private resolveNginxConfig(): string {
    return process.env.REACT_APP_NGINX_CONFIG || (this.resolveEnvironment() === "development" ? 'dev' : 'prod');
  }

  /**
   * Get the current configuration
   */
  getConfig(): EnvironmentConfig {
    return { ...this.config };
  }

  /**
   * Get API URL
   */
  getApiUrl(): string {
    return this.config.apiUrl;
  }

  /**
   * Get environment name
   */
  getEnvironment(): string {
    return this.config.environment;
  }

  /**
   * Check if proxy should be used
   */
  shouldUseProxy(): boolean {
    return this.config.useProxy;
  }

  /**
   * Check if debug mode is enabled
   */
  isDebugEnabled(): boolean {
    return this.config.debug;
  }

  /**
   * Check if we're in production
   */
  isProduction(): boolean {
    return this.config.environment === 'production';
  }

  /**
   * Check if we're in development
   */
  isDevelopment(): boolean {
    return this.config.environment === 'development';
  }

  /**
   * Get log level
   */
  getLogLevel(): string {
    return this.config.logLevel;
  }

  /**
   * Check if hot reload is enabled
   */
  isHotReloadEnabled(): boolean {
    return this.config.enableHotReload;
  }

  /**
   * Get backend URL
   */
  getBackendUrl(): string {
    return this.config.backendUrl;
  }

  /**
   * Get nginx config type
   */
  getNginxConfig(): string {
    return this.config.nginxConfig;
  }

  /**
   * Get timeout value
   */
  getTimeout(): number {
    return this.config.timeout;
  }

  /**
   * Get retry attempts
   */
  getRetryAttempts(): number {
    return this.config.retryAttempts;
  }

  /**
   * Log configuration (only in debug mode)
   */
  logConfig(): void {
    if (this.isDebugEnabled()) {
      console.log('🔧 Environment Configuration:', {
        environment: this.getEnvironment(),
        apiUrl: this.getApiUrl(),
        backendUrl: this.getBackendUrl(),
        useProxy: this.shouldUseProxy(),
        debug: this.isDebugEnabled(),
        logLevel: this.getLogLevel(),
        hotReload: this.isHotReloadEnabled(),
        nginxConfig: this.getNginxConfig(),
        timeout: this.getTimeout(),
        retryAttempts: this.getRetryAttempts()
      });
    }
  }
}

const environmentConfig = new EnvironmentConfiguration();
export default environmentConfig; 
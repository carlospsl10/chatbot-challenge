/**
 * Validation utilities for form inputs
 */

/**
 * Email validation regex pattern
 */
const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

/**
 * Validate email format
 * @param email - Email address to validate
 * @returns true if email is valid, false otherwise
 */
export const validateEmail = (email: string): boolean => {
  if (!email || typeof email !== 'string') {
    return false;
  }
  
  // Check basic format
  if (!EMAIL_REGEX.test(email)) {
    return false;
  }
  
  // Check length
  if (email.length > 254) {
    return false;
  }
  
  // Check for common invalid patterns
  if (email.startsWith('.') || email.endsWith('.') || email.includes('..')) {
    return false;
  }
  
  return true;
};

/**
 * Validate password strength
 * @param password - Password to validate
 * @returns object with isValid and message
 */
export const validatePassword = (password: string): { isValid: boolean; message: string } => {
  if (!password) {
    return { isValid: false, message: 'Password is required' };
  }
  
  if (password.length < 6) {
    return { isValid: false, message: 'Password must be at least 6 characters long' };
  }
  
  return { isValid: true, message: '' };
};

/**
 * Get email validation error message
 * @param email - Email to validate
 * @returns error message or empty string if valid
 */
export const getEmailError = (email: string): string => {
  if (!email) {
    return 'Email is required';
  }
  
  if (!validateEmail(email)) {
    return 'Please enter a valid email address';
  }
  
  return '';
};

/**
 * Get password validation error message
 * @param password - Password to validate
 * @returns error message or empty string if valid
 */
export const getPasswordError = (password: string): string => {
  return validatePassword(password).message;
};

/**
 * Check if form is valid
 * @param email - Email value
 * @param password - Password value
 * @returns true if form is valid
 */
export const isFormValid = (email: string, password: string): boolean => {
  return validateEmail(email) && validatePassword(password).isValid;
}; 
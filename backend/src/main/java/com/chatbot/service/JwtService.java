package com.chatbot.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generate JWT token for a customer
     * @param customerId the customer ID
     * @param email the customer email
     * @return JWT token string
     */
    public String generateToken(Long customerId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerId);
        claims.put("email", email);
        
        return createToken(claims, email);
    }
    
    /**
     * Create JWT token with claims
     * @param claims the claims to include in the token
     * @param subject the subject (email)
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extract customer ID from JWT token
     * @param token the JWT token
     * @return customer ID
     */
    public Long extractCustomerId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("customerId", Long.class);
    }
    
    /**
     * Extract email from JWT token
     * @param token the JWT token
     * @return email address
     */
    public String extractEmail(String token) {
        return extractSubject(token);
    }
    
    /**
     * Extract subject from JWT token
     * @param token the JWT token
     * @return subject (email)
     */
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * Extract expiration date from JWT token
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    /**
     * Extract all claims from JWT token
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Check if JWT token is expired
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validate JWT token
     * @param token the JWT token
     * @param email the email to validate against
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (email.equals(tokenEmail) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Get token expiration time
     * @return expiration time in milliseconds (actual timestamp)
     */
    public Long getExpirationTime() {
        return System.currentTimeMillis() + jwtExpiration;
    }
} 
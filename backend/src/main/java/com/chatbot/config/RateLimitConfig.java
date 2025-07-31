package com.chatbot.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {
    
    /**
     * Create a rate limiter bucket for chat API
     * Limits: 10 requests per minute per user
     * @return Bucket for rate limiting
     */
    @Bean
    public Bucket chatRateLimiter() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
    
    /**
     * Create a rate limiter bucket for OpenAI API
     * Limits: 50 requests per minute (OpenAI standard)
     * @return Bucket for OpenAI rate limiting
     */
    @Bean
    public Bucket openAiRateLimiter() {
        Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1)));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
} 
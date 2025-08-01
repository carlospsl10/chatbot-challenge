package com.chatbot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration to disable RAG components during testing
 * This prevents issues with PostgreSQL-specific features in H2 database
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    
    /**
     * Disable KnowledgeBaseInitializer for tests
     * This prevents RAG initialization which requires PostgreSQL features
     */
    @Bean
    @Primary
    public KnowledgeBaseInitializer testKnowledgeBaseInitializer() {
        return new KnowledgeBaseInitializer() {
            @Override
            public void run(String... args) throws Exception {
                // Do nothing - disable RAG initialization for tests
            }
        };
    }
} 
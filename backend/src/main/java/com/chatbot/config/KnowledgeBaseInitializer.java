package com.chatbot.config;

import com.chatbot.service.RagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeBaseInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseInitializer.class);
    
    @Autowired
    private RagService ragService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Starting knowledge base initialization...");
            
            // Initialize knowledge base with documents from resources
            ragService.initializeKnowledgeBase();
            
            logger.info("Knowledge base initialization completed successfully");
            
        } catch (Exception e) {
            logger.error("Error initializing knowledge base", e);
            // Don't fail the application startup, just log the error
        }
    }
} 
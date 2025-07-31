package com.chatbot.service;

import com.chatbot.model.KnowledgeBase;
import com.chatbot.repository.KnowledgeBaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RagService {
    
    private static final Logger logger = LoggerFactory.getLogger(RagService.class);
    
    @Autowired
    private OpenAiService openAiService;
    
    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Initialize knowledge base with documents from resources
     */
    public void initializeKnowledgeBase() {
        try {
            logger.info("Initializing knowledge base...");
            
            // Define the specific knowledge base files to load
            String[] knowledgeBaseFiles = {
                "order-status-overview.json",
                "status-transitions.json", 
                "fulfillment-workflow.json",
                "api-get-order-status.json",
                "api-get-customer-orders.json",
                "api-track-order.json",
                "shipping-methods.json"
            };
            
            for (String filename : knowledgeBaseFiles) {
                try {
                    Resource resource = resourceLoader.getResource("classpath:knowledge-base/" + filename);
                    if (resource.exists()) {
                        String content = new String(resource.getInputStream().readAllBytes());
                        Map<String, Object> document = objectMapper.readValue(content, Map.class);
                        
                        // Create knowledge base entry
                        KnowledgeBase kb = new KnowledgeBase(
                            (String) document.get("id"),
                            (String) document.get("title"),
                            (String) document.get("content"),
                            (String) document.get("category"),
                            objectMapper.writeValueAsString(document.get("tags"))
                        );
                        
                        // Generate embedding
                        String embedding = generateEmbedding(kb.getContent());
                        kb.setEmbedding(embedding);
                        
                        // Save to database
                        knowledgeBaseRepository.save(kb);
                        
                        // Update the vector column
                        updateVectorColumn(kb.getId(), embedding);
                        
                        logger.info("Loaded knowledge base document: {}", kb.getDocumentId());
                    } else {
                        logger.warn("Knowledge base file not found: {}", filename);
                    }
                    
                } catch (Exception e) {
                    logger.error("Error loading knowledge base document: {}", filename, e);
                }
            }
            
            logger.info("Knowledge base initialization completed");
            
        } catch (Exception e) {
            logger.error("Error initializing knowledge base", e);
        }
    }
    
    /**
     * Update the vector column for a knowledge base entry
     * @param id Knowledge base entry ID
     * @param embeddingJson JSON string of the embedding
     */
    private void updateVectorColumn(Long id, String embeddingJson) {
        try {
            String sql = "UPDATE knowledge_base SET embedding_vector = json_to_vector(?) WHERE id = ?";
            jdbcTemplate.update(sql, embeddingJson, id);
            logger.debug("Updated vector column for knowledge base entry: {}", id);
        } catch (Exception e) {
            logger.error("Error updating vector column for knowledge base entry: {}", id, e);
        }
    }
    
    /**
     * Generate embedding for text using OpenAI and convert to JSON string
     * @param text Text to embed
     * @return Embedding vector as JSON string
     */
    public String generateEmbedding(String text) {
        try {
            EmbeddingRequest request = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(Arrays.asList(text))
                .build();
            
            EmbeddingResult result = openAiService.createEmbeddings(request);
            List<Double> embedding = result.getData().get(0).getEmbedding();
            
            // Convert to JSON string for storage
            return objectMapper.writeValueAsString(embedding);
            
        } catch (Exception e) {
            logger.error("Error generating embedding", e);
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
    
    /**
     * Retrieve relevant knowledge base documents for a query
     * @param query User query
     * @param limit Maximum number of documents to retrieve
     * @return List of relevant knowledge base documents
     */
    public List<KnowledgeBase> retrieveRelevantDocuments(String query, int limit) {
        try {
            // Generate embedding for the query
            String queryEmbedding = generateEmbedding(query);
            
            // Search for similar documents
            List<Object[]> results = knowledgeBaseRepository.findSimilarDocuments(queryEmbedding, limit);
            
            // Convert results to KnowledgeBase objects
            List<KnowledgeBase> documents = new ArrayList<>();
            for (Object[] result : results) {
                try {
                    KnowledgeBase kb = new KnowledgeBase();
                    kb.setId((Long) result[0]);
                    kb.setDocumentId((String) result[1]);
                    kb.setTitle((String) result[2]);
                    kb.setContent((String) result[3]);
                    kb.setCategory((String) result[4]);
                    kb.setTags((String) result[5]);
                    kb.setEmbedding((String) result[6]);
                    
                    // Handle timestamp conversion safely
                    if (result[7] != null) {
                        if (result[7] instanceof java.sql.Timestamp) {
                            kb.setCreatedDate(((java.sql.Timestamp) result[7]).toLocalDateTime());
                        } else if (result[7] instanceof java.time.LocalDateTime) {
                            kb.setCreatedDate((java.time.LocalDateTime) result[7]);
                        }
                    }
                    
                    if (result[8] != null) {
                        if (result[8] instanceof java.sql.Timestamp) {
                            kb.setUpdatedDate(((java.sql.Timestamp) result[8]).toLocalDateTime());
                        } else if (result[8] instanceof java.time.LocalDateTime) {
                            kb.setUpdatedDate((java.time.LocalDateTime) result[8]);
                        }
                    }
                    
                    documents.add(kb);
                } catch (Exception e) {
                    logger.error("Error mapping result row: {}", Arrays.toString(result), e);
                }
            }
            
            logger.info("Retrieved {} relevant documents for query: {}", documents.size(), query);
            return documents;
            
        } catch (Exception e) {
            logger.error("Error retrieving relevant documents", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Retrieve relevant documents by category
     * @param query User query
     * @param category Category to filter by
     * @param limit Maximum number of documents to retrieve
     * @return List of relevant knowledge base documents
     */
    public List<KnowledgeBase> retrieveRelevantDocumentsByCategory(String query, String category, int limit) {
        try {
            // Generate embedding for the query
            String queryEmbedding = generateEmbedding(query);
            
            // Search for similar documents in category
            List<Object[]> results = knowledgeBaseRepository.findSimilarDocumentsByCategory(queryEmbedding, category, limit);
            
            // Convert results to KnowledgeBase objects
            List<KnowledgeBase> documents = new ArrayList<>();
            for (Object[] result : results) {
                try {
                    KnowledgeBase kb = new KnowledgeBase();
                    kb.setId((Long) result[0]);
                    kb.setDocumentId((String) result[1]);
                    kb.setTitle((String) result[2]);
                    kb.setContent((String) result[3]);
                    kb.setCategory((String) result[4]);
                    kb.setTags((String) result[5]);
                    kb.setEmbedding((String) result[6]);
                    
                    // Handle timestamp conversion safely
                    if (result[7] != null) {
                        if (result[7] instanceof java.sql.Timestamp) {
                            kb.setCreatedDate(((java.sql.Timestamp) result[7]).toLocalDateTime());
                        } else if (result[7] instanceof java.time.LocalDateTime) {
                            kb.setCreatedDate((java.time.LocalDateTime) result[7]);
                        }
                    }
                    
                    if (result[8] != null) {
                        if (result[8] instanceof java.sql.Timestamp) {
                            kb.setUpdatedDate(((java.sql.Timestamp) result[8]).toLocalDateTime());
                        } else if (result[8] instanceof java.time.LocalDateTime) {
                            kb.setUpdatedDate((java.time.LocalDateTime) result[8]);
                        }
                    }
                    
                    documents.add(kb);
                } catch (Exception e) {
                    logger.error("Error mapping result row: {}", Arrays.toString(result), e);
                }
            }
            
            logger.info("Retrieved {} relevant documents in category '{}' for query: {}", 
                       documents.size(), category, query);
            return documents;
            
        } catch (Exception e) {
            logger.error("Error retrieving relevant documents by category", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Build context from retrieved documents
     * @param documents List of knowledge base documents
     * @return Formatted context string
     */
    public String buildContext(List<KnowledgeBase> documents) {
        if (documents.isEmpty()) {
            return "No relevant information found.";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("Based on the following knowledge base information:\n\n");
        
        for (KnowledgeBase doc : documents) {
            context.append("Document: ").append(doc.getTitle()).append("\n");
            context.append("Category: ").append(doc.getCategory()).append("\n");
            context.append("Content: ").append(doc.getContent()).append("\n\n");
        }
        
        return context.toString();
    }
    
    /**
     * Get all knowledge base documents
     * @return List of all knowledge base documents
     */
    public List<KnowledgeBase> getAllDocuments() {
        return knowledgeBaseRepository.findAll();
    }
    
    /**
     * Get documents by category
     * @param category Category to filter by
     * @return List of knowledge base documents
     */
    public List<KnowledgeBase> getDocumentsByCategory(String category) {
        return knowledgeBaseRepository.findByCategory(category);
    }
} 
package com.chatbot.repository;

import com.chatbot.model.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    
    /**
     * Find knowledge base entries by category
     * @param category Category to search for
     * @return List of knowledge base entries
     */
    List<KnowledgeBase> findByCategory(String category);
    
    /**
     * Find knowledge base entries by document ID
     * @param documentId Document ID to search for
     * @return Knowledge base entry
     */
    KnowledgeBase findByDocumentId(String documentId);
    
    /**
     * Find knowledge base entries by tags (JSON array contains)
     * @param tag Tag to search for
     * @return List of knowledge base entries
     */
    @Query("SELECT kb FROM KnowledgeBase kb WHERE kb.tags LIKE %:tag%")
    List<KnowledgeBase> findByTag(@Param("tag") String tag);
    
    /**
     * Vector similarity search using cosine similarity
     * @param embedding Query embedding vector
     * @param limit Maximum number of results
     * @return List of similar knowledge base entries
     */
    @Query(value = "SELECT kb.id, kb.document_id, kb.title, kb.content, kb.category, kb.tags, kb.embedding, kb.created_date, kb.updated_date, " +
                   "(kb.embedding_vector <=> json_to_vector(:embedding)) as similarity " +
                   "FROM knowledge_base kb " +
                   "WHERE kb.embedding_vector IS NOT NULL " +
                   "ORDER BY kb.embedding_vector <=> json_to_vector(:embedding) " +
                   "LIMIT :limit", nativeQuery = true)
    List<Object[]> findSimilarDocuments(@Param("embedding") String embedding, @Param("limit") int limit);
    
    /**
     * Vector similarity search with category filter
     * @param embedding Query embedding vector
     * @param category Category to filter by
     * @param limit Maximum number of results
     * @return List of similar knowledge base entries
     */
    @Query(value = "SELECT kb.id, kb.document_id, kb.title, kb.content, kb.category, kb.tags, kb.embedding, kb.created_date, kb.updated_date, " +
                   "(kb.embedding_vector <=> json_to_vector(:embedding)) as similarity " +
                   "FROM knowledge_base kb " +
                   "WHERE kb.category = :category AND kb.embedding_vector IS NOT NULL " +
                   "ORDER BY kb.embedding_vector <=> json_to_vector(:embedding) " +
                   "LIMIT :limit", nativeQuery = true)
    List<Object[]> findSimilarDocumentsByCategory(@Param("embedding") String embedding, 
                                                @Param("category") String category, 
                                                @Param("limit") int limit);
} 
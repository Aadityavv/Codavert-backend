package com.codavert.repository;

import com.codavert.entity.ProjectDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDocumentRepository extends JpaRepository<ProjectDocument, Long> {
    
    List<ProjectDocument> findByProjectId(Long projectId);
    
    Page<ProjectDocument> findByProjectId(Long projectId, Pageable pageable);
    
    List<ProjectDocument> findByUserId(Long userId);
    
    Page<ProjectDocument> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT pd FROM ProjectDocument pd WHERE pd.project.id = :projectId AND pd.type = :type")
    List<ProjectDocument> findByProjectIdAndType(@Param("projectId") Long projectId, 
                                                 @Param("type") ProjectDocument.DocumentType type);
    
    @Query("SELECT pd FROM ProjectDocument pd WHERE pd.project.id = :projectId AND pd.status = :status")
    Page<ProjectDocument> findByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                                  @Param("status") ProjectDocument.DocumentStatus status, 
                                                  Pageable pageable);
    
    @Query("SELECT pd FROM ProjectDocument pd WHERE pd.user.id = :userId AND " +
           "(LOWER(pd.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pd.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<ProjectDocument> searchDocumentsByUserId(@Param("userId") Long userId, 
                                                 @Param("searchTerm") String searchTerm, 
                                                 Pageable pageable);
    
    @Query("SELECT COUNT(pd) FROM ProjectDocument pd WHERE pd.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(pd) FROM ProjectDocument pd WHERE pd.project.id = :projectId AND pd.type = :type")
    Long countByProjectIdAndType(@Param("projectId") Long projectId, 
                                @Param("type") ProjectDocument.DocumentType type);
    
    @Query("SELECT pd FROM ProjectDocument pd WHERE pd.project.client.id = :clientId AND pd.project.user.id = :userId")
    List<ProjectDocument> findByClientIdAndUserId(@Param("clientId") Long clientId, 
                                                  @Param("userId") Long userId);
    
    @Query("SELECT COUNT(pd) FROM ProjectDocument pd WHERE pd.project.client.id = :clientId AND pd.project.user.id = :userId")
    Long countByClientIdAndUserId(@Param("clientId") Long clientId, 
                                 @Param("userId") Long userId);
}

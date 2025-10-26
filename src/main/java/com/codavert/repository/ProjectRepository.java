package com.codavert.repository;

import com.codavert.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByUserId(Long userId);
    
    Page<Project> findByUserId(Long userId, Pageable pageable);
    
    List<Project> findByClientId(Long clientId);
    
    Page<Project> findByClientId(Long clientId, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND p.status = :status")
    Page<Project> findByUserIdAndStatus(@Param("userId") Long userId, 
                                       @Param("status") Project.ProjectStatus status, 
                                       Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND p.type = :type")
    Page<Project> findByUserIdAndType(@Param("userId") Long userId, 
                                     @Param("type") Project.ProjectType type, 
                                     Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.technologies) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Project> searchProjectsByUserId(@Param("userId") Long userId, 
                                        @Param("searchTerm") String searchTerm, 
                                        Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.user.id = :userId AND p.endDate < :date")
    Page<Project> findOverdueProjects(@Param("userId") Long userId, 
                                     @Param("date") LocalDate date, 
                                     Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.user.id = :userId AND p.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, 
                               @Param("status") Project.ProjectStatus status);
    
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.user.id = :userId AND p.status = :status")
    Double sumBudgetByUserIdAndStatus(@Param("userId") Long userId, 
                                     @Param("status") Project.ProjectStatus status);
}

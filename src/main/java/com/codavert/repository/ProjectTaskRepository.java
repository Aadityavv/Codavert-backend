package com.codavert.repository;

import com.codavert.entity.ProjectTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    
    List<ProjectTask> findByProjectId(Long projectId);
    
    Page<ProjectTask> findByProjectId(Long projectId, Pageable pageable);
    
    List<ProjectTask> findByAssignedUserId(Long assignedUserId);
    
    Page<ProjectTask> findByAssignedUserId(Long assignedUserId, Pageable pageable);
    
    @Query("SELECT pt FROM ProjectTask pt WHERE pt.project.id = :projectId AND pt.status = :status")
    Page<ProjectTask> findByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                              @Param("status") ProjectTask.TaskStatus status, 
                                              Pageable pageable);
    
    @Query("SELECT pt FROM ProjectTask pt WHERE pt.assignedUser.id = :userId AND pt.status = :status")
    Page<ProjectTask> findByAssignedUserIdAndStatus(@Param("userId") Long userId, 
                                                   @Param("status") ProjectTask.TaskStatus status, 
                                                   Pageable pageable);
    
    @Query("SELECT pt FROM ProjectTask pt WHERE pt.project.id = :projectId AND pt.dueDate < :date AND pt.status != 'COMPLETED'")
    List<ProjectTask> findOverdueTasksByProjectId(@Param("projectId") Long projectId, 
                                                 @Param("date") LocalDate date);
    
    @Query("SELECT pt FROM ProjectTask pt WHERE pt.assignedUser.id = :userId AND pt.dueDate < :date AND pt.status != 'COMPLETED'")
    List<ProjectTask> findOverdueTasksByUserId(@Param("userId") Long userId, 
                                             @Param("date") LocalDate date);
    
    @Query("SELECT pt FROM ProjectTask pt WHERE pt.project.id = :projectId AND " +
           "(LOWER(pt.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(pt.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<ProjectTask> searchTasksByProjectId(@Param("projectId") Long projectId, 
                                            @Param("searchTerm") String searchTerm, 
                                            Pageable pageable);
    
    @Query("SELECT COUNT(pt) FROM ProjectTask pt WHERE pt.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(pt) FROM ProjectTask pt WHERE pt.project.id = :projectId AND pt.status = :status")
    Long countByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                  @Param("status") ProjectTask.TaskStatus status);
    
    @Query("SELECT SUM(pt.actualHours) FROM ProjectTask pt WHERE pt.project.id = :projectId")
    Integer sumActualHoursByProjectId(@Param("projectId") Long projectId);
}

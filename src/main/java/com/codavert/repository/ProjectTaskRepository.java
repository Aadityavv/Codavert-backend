package com.codavert.repository;

import com.codavert.entity.ProjectTask;
import com.codavert.entity.ProjectTask.TaskStatus;
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
    
    // Find all tasks for a project
    Page<ProjectTask> findByProjectId(Long projectId, Pageable pageable);
    
    List<ProjectTask> findByProjectId(Long projectId);
    
    // Find tasks by project and status
    List<ProjectTask> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    
    // Find tasks assigned to a user
    Page<ProjectTask> findByAssignedToUserId(Long userId, Pageable pageable);
    
    // Find tasks by project and assigned user
    List<ProjectTask> findByProjectIdAndAssignedToUserId(Long projectId, Long userId);
    
    // Count tasks by project
    long countByProjectId(Long projectId);
    
    // Count tasks by project and status
    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
    
    // Find overdue tasks
    @Query("SELECT t FROM ProjectTask t WHERE t.projectId = :projectId " +
           "AND t.dueDate < :currentDate AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<ProjectTask> findOverdueTasks(@Param("projectId") Long projectId, 
                                      @Param("currentDate") LocalDate currentDate);
    
    // Get task statistics for a project
    @Query("SELECT t.status as status, COUNT(t) as count FROM ProjectTask t " +
           "WHERE t.projectId = :projectId GROUP BY t.status")
    List<Object[]> getTaskStatsByProject(@Param("projectId") Long projectId);
    
    // Get upcoming tasks (due in next N days)
    @Query("SELECT t FROM ProjectTask t WHERE t.projectId = :projectId " +
           "AND t.dueDate BETWEEN :startDate AND :endDate " +
           "AND t.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY t.dueDate ASC")
    List<ProjectTask> findUpcomingTasks(@Param("projectId") Long projectId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    // Delete all tasks for a project
    void deleteByProjectId(Long projectId);
}

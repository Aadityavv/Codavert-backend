package com.codavert.controller;

import com.codavert.dto.ProjectTaskDto;
import com.codavert.entity.ProjectTask;
import com.codavert.entity.ProjectTask.TaskStatus;
import com.codavert.repository.ProjectTaskRepository;
import com.codavert.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Project Tasks", description = "Task management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectTaskController {
    
    @Autowired
    private ProjectTaskRepository taskRepository;
    
    @Autowired
    private ActivityLogService activityLogService;
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks for a project")
    public ResponseEntity<Page<ProjectTask>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ProjectTask> tasks = taskRepository.findByProjectId(projectId, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ProjectTask> getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<ProjectTask> createTask(
            @Valid @RequestBody ProjectTaskDto taskDto,
            @RequestParam(required = false) Long userId) {
        ProjectTask task = new ProjectTask();
        task.setProjectId(taskDto.getProjectId());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus() != null ? taskDto.getStatus() : TaskStatus.TODO);
        task.setPriority(taskDto.getPriority());
        task.setAssignedToUserId(taskDto.getAssignedToUserId());
        task.setStartDate(taskDto.getStartDate());
        task.setDueDate(taskDto.getDueDate());
        task.setEstimatedHours(taskDto.getEstimatedHours());
        task.setActualHours(taskDto.getActualHours());
        
        ProjectTask savedTask = taskRepository.save(task);
        
        // Log activity
        if (userId != null) {
            activityLogService.logTaskCreated(userId, savedTask.getId(), savedTask.getTitle());
        }
        
        return ResponseEntity.ok(savedTask);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<ProjectTask> updateTask(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody ProjectTaskDto taskDto) {
        
        return taskRepository.findById(id)
                .map(task -> {
                    TaskStatus oldStatus = task.getStatus();
                    
                    if (taskDto.getTitle() != null) task.setTitle(taskDto.getTitle());
                    if (taskDto.getDescription() != null) task.setDescription(taskDto.getDescription());
                    if (taskDto.getStatus() != null) task.setStatus(taskDto.getStatus());
                    if (taskDto.getPriority() != null) task.setPriority(taskDto.getPriority());
                    if (taskDto.getAssignedToUserId() != null) task.setAssignedToUserId(taskDto.getAssignedToUserId());
                    if (taskDto.getStartDate() != null) task.setStartDate(taskDto.getStartDate());
                    if (taskDto.getDueDate() != null) task.setDueDate(taskDto.getDueDate());
                    if (taskDto.getEstimatedHours() != null) task.setEstimatedHours(taskDto.getEstimatedHours());
                    if (taskDto.getActualHours() != null) task.setActualHours(taskDto.getActualHours());
                    
                    ProjectTask updatedTask = taskRepository.save(task);
                    
                    // Log activity
                    if (userId != null) {
                        if (taskDto.getStatus() != null && !taskDto.getStatus().equals(oldStatus)) {
                            if (taskDto.getStatus() == TaskStatus.COMPLETED) {
                                activityLogService.logTaskCompleted(userId, updatedTask.getId(), updatedTask.getTitle());
                            } else {
                                activityLogService.logTaskStatusChanged(userId, updatedTask.getId(), 
                                    updatedTask.getTitle(), taskDto.getStatus().toString());
                            }
                        } else {
                            activityLogService.logTaskUpdated(userId, updatedTask.getId(), updatedTask.getTitle());
                        }
                    }
                    
                    return ResponseEntity.ok(updatedTask);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/project/{projectId}/statistics")
    @Operation(summary = "Get task statistics for a project")
    public ResponseEntity<Map<String, Object>> getProjectTaskStatistics(@PathVariable Long projectId) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalTasks = taskRepository.countByProjectId(projectId);
        long todoTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);
        long blockedTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.BLOCKED);
        
        stats.put("totalTasks", totalTasks);
        stats.put("todoTasks", todoTasks);
        stats.put("inProgressTasks", inProgressTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("blockedTasks", blockedTasks);
        
        // Calculate progress percentage
        double progress = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        stats.put("progressPercentage", Math.round(progress * 10.0) / 10.0);
        
        // Get overdue tasks
        List<ProjectTask> overdueTasks = taskRepository.findOverdueTasks(projectId, LocalDate.now());
        stats.put("overdueTasks", overdueTasks.size());
        
        // Get upcoming tasks (next 7 days)
        List<ProjectTask> upcomingTasks = taskRepository.findUpcomingTasks(
            projectId,
            LocalDate.now(),
            LocalDate.now().plusDays(7)
        );
        stats.put("upcomingTasks", upcomingTasks.size());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/project/{projectId}/by-status")
    @Operation(summary = "Get tasks by project and status")
    public ResponseEntity<List<ProjectTask>> getTasksByStatus(
            @PathVariable Long projectId,
            @RequestParam TaskStatus status) {
        
        List<ProjectTask> tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/project/{projectId}/overdue")
    @Operation(summary = "Get overdue tasks for a project")
    public ResponseEntity<List<ProjectTask>> getOverdueTasks(@PathVariable Long projectId) {
        List<ProjectTask> tasks = taskRepository.findOverdueTasks(projectId, LocalDate.now());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get tasks assigned to a user")
    public ResponseEntity<Page<ProjectTask>> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        Page<ProjectTask> tasks = taskRepository.findByAssignedToUserId(userId, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<ProjectTask> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            @RequestParam(required = false) Long userId) {
        
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(status);
                    ProjectTask updatedTask = taskRepository.save(task);
                    
                    // Log activity
                    if (userId != null) {
                        if (status == TaskStatus.COMPLETED) {
                            activityLogService.logTaskCompleted(userId, updatedTask.getId(), updatedTask.getTitle());
                        } else {
                            activityLogService.logTaskStatusChanged(userId, updatedTask.getId(), 
                                updatedTask.getTitle(), status.toString());
                        }
                    }
                    
                    return ResponseEntity.ok(updatedTask);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


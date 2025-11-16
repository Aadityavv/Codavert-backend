package com.codavert.controller;

import com.codavert.dto.ProjectDto;
import com.codavert.entity.Project;
import com.codavert.service.ProjectService;
import com.codavert.service.ActivityLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ActivityLogService activityLogService;
    
    @GetMapping
    public ResponseEntity<Page<Project>> getAllProjects(@RequestParam Long userId, Pageable pageable) {
        Page<Project> projects = projectService.getAllProjectsByUserId(userId, pageable);
        return ResponseEntity.ok(projects);
    }

    // Admin-only: fetch all projects
    @GetMapping("/admin")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Project>> getAllProjectsForAdmin(Pageable pageable) {
        Page<Project> projects = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
            .map(project -> ResponseEntity.ok(project))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectDto projectDto, 
                                               @RequestParam Long userId) {
        // Allow frontend to send name instead of title
        if (projectDto.getTitle() == null && projectDto.getName() != null) {
            projectDto.setTitle(projectDto.getName());
        }
        Project project = projectService.createProject(projectDto, userId);
        
        // Log activity
        activityLogService.logProjectCreated(userId, project.getId(), project.getTitle());
        
        return ResponseEntity.ok(project);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, 
                                          @RequestParam Long userId,
                                          @Valid @RequestBody ProjectDto projectDto) {
        try {
            Project project = projectService.updateProject(id, projectDto);
            
            // Log activity
            activityLogService.logProjectUpdated(userId, project.getId(), project.getTitle());
            
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id,
                                          @RequestParam Long userId,
                                          @RequestParam(required = false) String projectName) {
        try {
            // Log activity before deletion
            if (projectName != null) {
                activityLogService.logProjectDeleted(userId, id, projectName);
            }
            
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Project>> searchProjects(@RequestParam Long userId,
                                                       @RequestParam String searchTerm,
                                                       Pageable pageable) {
        Page<Project> projects = projectService.searchProjects(userId, searchTerm, pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Project>> getProjectsByStatus(@RequestParam Long userId,
                                                            @PathVariable String status,
                                                            Pageable pageable) {
        Page<Project> projects = projectService.getProjectsByStatus(userId, status, pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Project>> getProjectsByType(@RequestParam Long userId,
                                                          @PathVariable String type,
                                                          Pageable pageable) {
        Page<Project> projects = projectService.getProjectsByType(userId, type, pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<Project>> getOverdueProjects(@RequestParam Long userId) {
        List<Project> projects = projectService.getOverdueProjects(userId);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getProjectCount(@RequestParam Long userId) {
        Long count = projectService.getProjectCountByUserId(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getProjectCountByStatus(@RequestParam Long userId,
                                                       @PathVariable String status) {
        Long count = projectService.getProjectCountByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/budget/total")
    public ResponseEntity<Double> getTotalBudgetByStatus(@RequestParam Long userId,
                                                        @RequestParam String status) {
        Double total = projectService.getTotalBudgetByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<Project>> getProjectsByAssignedEmployee(
            @PathVariable Long employeeId,
            Pageable pageable) {
        Page<Project> projects = projectService.getProjectsByAssignedEmployee(employeeId, pageable);
        return ResponseEntity.ok(projects);
    }
}

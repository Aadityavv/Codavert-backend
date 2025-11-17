package com.codavert.dto;

import com.codavert.entity.ProjectTask;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignedTaskViewDto {
    public Long taskId;
    public String title;
    public String description;
    public String status;
    public String priority;
    public LocalDate startDate;
    public LocalDate dueDate;
    public LocalDateTime completedAt;
    public Double estimatedHours;
    public Double actualHours;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    
    public Long projectId;
    public String projectTitle;
    
    public Long employeeId;
    public String employeeFirstName;
    public String employeeLastName;
    public String employeeEmail;
    public String employeePhone;
    
    public static AssignedTaskViewDto from(ProjectTask task, String projectTitle,
                                           Long employeeId, String firstName, String lastName,
                                           String email, String phone) {
        AssignedTaskViewDto dto = new AssignedTaskViewDto();
        dto.taskId = task.getId();
        dto.title = task.getTitle();
        dto.description = task.getDescription();
        dto.status = task.getStatus() != null ? task.getStatus().name() : null;
        dto.priority = task.getPriority() != null ? task.getPriority().name() : null;
        dto.startDate = task.getStartDate();
        dto.dueDate = task.getDueDate();
        dto.completedAt = task.getCompletedAt();
        dto.estimatedHours = task.getEstimatedHours();
        dto.actualHours = task.getActualHours();
        dto.createdAt = task.getCreatedAt();
        dto.updatedAt = task.getUpdatedAt();
        
        dto.projectId = task.getProject() != null ? task.getProject().getId() : null;
        dto.projectTitle = projectTitle;
        
        dto.employeeId = employeeId;
        dto.employeeFirstName = firstName;
        dto.employeeLastName = lastName;
        dto.employeeEmail = email;
        dto.employeePhone = phone;
        return dto;
    }
}




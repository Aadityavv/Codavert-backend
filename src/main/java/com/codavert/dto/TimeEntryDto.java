package com.codavert.dto;

import com.codavert.entity.TimeEntry;

import java.time.LocalDateTime;

public class TimeEntryDto {
    private Long id;
    private Long userId;
    private Long projectId;
    private Long taskId;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double hoursLogged;
    private Boolean isBillable;
    private TimeEntry.EntryStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimeEntryDto() {
    }

    public TimeEntryDto(Long id, Long userId, Long projectId, Long taskId, String description,
                        LocalDateTime startTime, LocalDateTime endTime, Double hoursLogged, Boolean isBillable,
                        TimeEntry.EntryStatus status, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hoursLogged = hoursLogged;
        this.isBillable = isBillable;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TimeEntryDto fromEntity(TimeEntry entity) {
        return new TimeEntryDto(
                entity.getId(),
                entity.getUserId(),
                entity.getProjectId(),
                entity.getTaskId(),
                entity.getDescription(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getHoursLogged(),
                entity.getIsBillable(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getHoursLogged() {
        return hoursLogged;
    }

    public void setHoursLogged(Double hoursLogged) {
        this.hoursLogged = hoursLogged;
    }

    public Boolean getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(Boolean isBillable) {
        this.isBillable = isBillable;
    }

    public TimeEntry.EntryStatus getStatus() {
        return status;
    }

    public void setStatus(TimeEntry.EntryStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

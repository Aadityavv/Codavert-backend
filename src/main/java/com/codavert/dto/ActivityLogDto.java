package com.codavert.dto;

import com.codavert.entity.ActivityLog;

import java.time.LocalDateTime;

public class ActivityLogDto {
    private Long id;
    private Long userId;
    private ActivityLog.ActivityType activityType;
    private ActivityLog.EntityType entityType;
    private Long entityId;
    private String description;
    private String metadata;
    private LocalDateTime createdAt;

    public ActivityLogDto() {
    }

    public ActivityLogDto(Long id, Long userId, ActivityLog.ActivityType activityType, ActivityLog.EntityType entityType,
                          Long entityId, String description, String metadata, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.activityType = activityType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }

    public static ActivityLogDto fromEntity(ActivityLog entity) {
        return new ActivityLogDto(
                entity.getId(),
                entity.getUserId(),
                entity.getActivityType(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getDescription(),
                entity.getMetadata(),
                entity.getCreatedAt()
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

    public ActivityLog.ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityLog.ActivityType activityType) {
        this.activityType = activityType;
    }

    public ActivityLog.EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(ActivityLog.EntityType entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

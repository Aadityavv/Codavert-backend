package com.codavert.service;

import com.codavert.entity.ActivityLog;
import com.codavert.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Transactional
    public ActivityLog logActivity(Long userId, 
                                   ActivityLog.ActivityType activityType,
                                   ActivityLog.EntityType entityType,
                                   Long entityId,
                                   String description) {
        ActivityLog log = new ActivityLog(userId, activityType, entityType, entityId, description);
        return activityLogRepository.save(log);
    }

    @Transactional
    public ActivityLog logActivity(Long userId, 
                                   ActivityLog.ActivityType activityType,
                                   ActivityLog.EntityType entityType,
                                   Long entityId,
                                   String description,
                                   String metadata) {
        ActivityLog log = new ActivityLog(userId, activityType, entityType, entityId, description);
        log.setMetadata(metadata);
        return activityLogRepository.save(log);
    }

    public Page<ActivityLog> getUserActivities(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<ActivityLog> getRecentActivities(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).getContent();
    }

    public List<ActivityLog> getActivitiesSince(Long userId, LocalDateTime since) {
        return activityLogRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, since);
    }

    public Page<ActivityLog> getActivitiesByEntityType(Long userId, 
                                                       ActivityLog.EntityType entityType, 
                                                       int page, 
                                                       int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activityLogRepository.findByUserIdAndEntityTypeOrderByCreatedAtDesc(userId, entityType, pageable);
    }

    public long countRecentActivities(Long userId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityLogRepository.countByUserIdAndCreatedAtAfter(userId, since);
    }

    // Helper methods for common activity types
    public void logProjectCreated(Long userId, Long projectId, String projectName) {
        logActivity(userId, ActivityLog.ActivityType.CREATED, ActivityLog.EntityType.PROJECT, 
                    projectId, "Created project: " + projectName);
    }

    public void logProjectUpdated(Long userId, Long projectId, String projectName) {
        logActivity(userId, ActivityLog.ActivityType.UPDATED, ActivityLog.EntityType.PROJECT,
                    projectId, "Updated project: " + projectName);
    }

    public void logProjectDeleted(Long userId, Long projectId, String projectName) {
        logActivity(userId, ActivityLog.ActivityType.DELETED, ActivityLog.EntityType.PROJECT,
                    projectId, "Deleted project: " + projectName);
    }

    public void logTaskCreated(Long userId, Long taskId, String taskTitle) {
        logActivity(userId, ActivityLog.ActivityType.CREATED, ActivityLog.EntityType.TASK,
                    taskId, "Created task: " + taskTitle);
    }

    public void logTaskUpdated(Long userId, Long taskId, String taskTitle) {
        logActivity(userId, ActivityLog.ActivityType.UPDATED, ActivityLog.EntityType.TASK,
                    taskId, "Updated task: " + taskTitle);
    }

    public void logTaskCompleted(Long userId, Long taskId, String taskTitle) {
        logActivity(userId, ActivityLog.ActivityType.COMPLETED, ActivityLog.EntityType.TASK,
                    taskId, "Completed task: " + taskTitle);
    }

    public void logTaskStatusChanged(Long userId, Long taskId, String taskTitle, String newStatus) {
        logActivity(userId, ActivityLog.ActivityType.STATUS_CHANGED, ActivityLog.EntityType.TASK,
                    taskId, "Changed task status to " + newStatus + ": " + taskTitle);
    }

    public void logClientCreated(Long userId, Long clientId, String clientName) {
        logActivity(userId, ActivityLog.ActivityType.CREATED, ActivityLog.EntityType.CLIENT,
                    clientId, "Created client: " + clientName);
    }

    public void logClientUpdated(Long userId, Long clientId, String clientName) {
        logActivity(userId, ActivityLog.ActivityType.UPDATED, ActivityLog.EntityType.CLIENT,
                    clientId, "Updated client: " + clientName);
    }

    public void logInvoiceCreated(Long userId, Long invoiceId, String invoiceNumber) {
        logActivity(userId, ActivityLog.ActivityType.CREATED, ActivityLog.EntityType.INVOICE,
                    invoiceId, "Created invoice: " + invoiceNumber);
    }

    public void logDocumentGenerated(Long userId, Long documentId, String documentType) {
        logActivity(userId, ActivityLog.ActivityType.CREATED, ActivityLog.EntityType.DOCUMENT,
                    documentId, "Generated " + documentType + " document");
    }
}

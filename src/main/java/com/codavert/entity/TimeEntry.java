package com.codavert.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_entries")
@EntityListeners(AuditingEntityListener.class)
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private ProjectTask task;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double hoursLogged;

    private Boolean isBillable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;

    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = EntryStatus.DRAFT;
        }
        if (isBillable == null) {
            isBillable = true;
        }
        calculateHoursLogged();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateHoursLogged();
    }

    private void calculateHoursLogged() {
        if (startTime != null && endTime != null) {
            long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
            hoursLogged = durationMinutes / 60.0;
        }
    }

    public enum EntryStatus {
        DRAFT,
        SUBMITTED,
        APPROVED,
        REJECTED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getProjectId() {
        return project != null ? project.getId() : null;
    }

    public ProjectTask getTask() {
        return task;
    }

    public void setTask(ProjectTask task) {
        this.task = task;
    }

    public Long getTaskId() {
        return task != null ? task.getId() : null;
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

    public EntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntryStatus status) {
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

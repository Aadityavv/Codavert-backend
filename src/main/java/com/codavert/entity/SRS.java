package com.codavert.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "srs")
@EntityListeners(AuditingEntityListener.class)
public class SRS {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String srsNumber;
    
    @NotNull
    @Column(name = "srs_date")
    private LocalDate srsDate;
    
    @Size(max = 20)
    private String version;
    
    @Size(max = 200)
    @Column(name = "project_name")
    private String projectName;
    
    @Size(max = 100)
    @Column(name = "project_manager")
    private String projectManager;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private SRSStatus status = SRSStatus.DRAFT;
    
    @Column(name = "document_data", columnDefinition = "TEXT")
    private String documentData; // JSON string of full SRS data
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public SRS() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSrsNumber() {
        return srsNumber;
    }
    
    public void setSrsNumber(String srsNumber) {
        this.srsNumber = srsNumber;
    }
    
    public LocalDate getSrsDate() {
        return srsDate;
    }
    
    public void setSrsDate(LocalDate srsDate) {
        this.srsDate = srsDate;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getProjectManager() {
        return projectManager;
    }
    
    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }
    
    public SRSStatus getStatus() {
        return status;
    }
    
    public void setStatus(SRSStatus status) {
        this.status = status;
    }
    
    public String getDocumentData() {
        return documentData;
    }
    
    public void setDocumentData(String documentData) {
        this.documentData = documentData;
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
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // Enums
    public enum SRSStatus {
        DRAFT, REVIEW, APPROVED, REJECTED, VERSIONED, CANCELLED
    }
}


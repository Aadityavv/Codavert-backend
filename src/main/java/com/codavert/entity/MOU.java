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
@Table(name = "mous")
@EntityListeners(AuditingEntityListener.class)
public class MOU {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String mouNumber;
    
    @NotNull
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Size(max = 200)
    @Column(name = "project_title")
    private String projectTitle;
    
    @Size(max = 1000)
    @Column(name = "project_description")
    private String projectDescription;
    
    @Size(max = 100)
    private String duration;
    
    @Size(max = 200)
    private String budget;
    
    @Size(max = 100)
    @Column(name = "party_a_signatory")
    private String partyASignatory;
    
    @Size(max = 100)
    @Column(name = "party_b_signatory")
    private String partyBSignatory;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private MOUStatus status = MOUStatus.DRAFT;
    
    @Column(name = "document_data", columnDefinition = "TEXT")
    private String documentData; // JSON string of full MOU data
    
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
    public MOU() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMouNumber() {
        return mouNumber;
    }
    
    public void setMouNumber(String mouNumber) {
        this.mouNumber = mouNumber;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public String getProjectTitle() {
        return projectTitle;
    }
    
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }
    
    public String getProjectDescription() {
        return projectDescription;
    }
    
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getBudget() {
        return budget;
    }
    
    public void setBudget(String budget) {
        this.budget = budget;
    }
    
    public String getPartyASignatory() {
        return partyASignatory;
    }
    
    public void setPartyASignatory(String partyASignatory) {
        this.partyASignatory = partyASignatory;
    }
    
    public String getPartyBSignatory() {
        return partyBSignatory;
    }
    
    public void setPartyBSignatory(String partyBSignatory) {
        this.partyBSignatory = partyBSignatory;
    }
    
    public MOUStatus getStatus() {
        return status;
    }
    
    public void setStatus(MOUStatus status) {
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
    public enum MOUStatus {
        DRAFT, SIGNED, ACTIVE, EXPIRED, TERMINATED, CANCELLED
    }
}


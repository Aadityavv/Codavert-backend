package com.codavert.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class MOUDto {
    
    private Long id;
    
    @Size(max = 50)
    private String mouNumber;
    
    @NotNull
    private LocalDate effectiveDate;
    
    private LocalDate startDate;
    
    @NotNull
    private Long clientId;
    
    private Long projectId;
    
    private Long userId;
    
    @Size(max = 200)
    private String projectTitle;
    
    @Size(max = 1000)
    private String projectDescription;
    
    @Size(max = 100)
    private String duration;
    
    @Size(max = 200)
    private String budget;
    
    @Size(max = 100)
    private String partyASignatory;
    
    @Size(max = 100)
    private String partyBSignatory;
    
    private String status;
    
    private String documentData; // JSON string
    
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
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDocumentData() {
        return documentData;
    }
    
    public void setDocumentData(String documentData) {
        this.documentData = documentData;
    }
}


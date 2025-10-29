package com.codavert.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SRSDto {
    
    private Long id;
    
    @Size(max = 50)
    private String srsNumber;
    
    private LocalDate srsDate;
    
    @Size(max = 20)
    private String version;
    
    @NotNull
    private Long clientId;
    
    private Long projectId;
    
    private Long userId;
    
    @Size(max = 200)
    private String projectName;
    
    @Size(max = 100)
    private String projectManager;
    
    private String status;
    
    private String documentData; // JSON string
    
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


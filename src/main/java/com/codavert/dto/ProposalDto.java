package com.codavert.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProposalDto {
    
    private Long id;
    
    @Size(max = 50)
    private String proposalNumber;
    
    private LocalDate proposalDate;
    
    private LocalDate validUntil;
    
    @NotNull
    private Long clientId;
    
    private Long projectId;
    
    private Long userId;
    
    @Size(max = 200)
    private String projectTitle;
    
    private BigDecimal totalCost;
    
    @Size(max = 500)
    private String paymentTerms;
    
    private String status;
    
    private String documentData; // JSON string
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProposalNumber() {
        return proposalNumber;
    }
    
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }
    
    public LocalDate getProposalDate() {
        return proposalDate;
    }
    
    public void setProposalDate(LocalDate proposalDate) {
        this.proposalDate = proposalDate;
    }
    
    public LocalDate getValidUntil() {
        return validUntil;
    }
    
    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
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
    
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    
    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
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


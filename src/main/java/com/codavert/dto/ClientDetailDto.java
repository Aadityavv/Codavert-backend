package com.codavert.dto;

import com.codavert.entity.ProjectDocument;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ClientDetailDto {
    
    private ClientDto client;
    private ClientStatisticsDto statistics;
    private List<ProjectDto> projects;
    private List<InvoiceDto> invoices;
    private List<ProjectDocumentDto> documents;
    
    // Constructors
    public ClientDetailDto() {}
    
    public ClientDetailDto(ClientDto client, ClientStatisticsDto statistics, 
                          List<ProjectDto> projects, List<InvoiceDto> invoices,
                          List<ProjectDocumentDto> documents) {
        this.client = client;
        this.statistics = statistics;
        this.projects = projects;
        this.invoices = invoices;
        this.documents = documents;
    }
    
    // Getters and Setters
    public ClientDto getClient() {
        return client;
    }
    
    public void setClient(ClientDto client) {
        this.client = client;
    }
    
    public ClientStatisticsDto getStatistics() {
        return statistics;
    }
    
    public void setStatistics(ClientStatisticsDto statistics) {
        this.statistics = statistics;
    }
    
    public List<ProjectDto> getProjects() {
        return projects;
    }
    
    public void setProjects(List<ProjectDto> projects) {
        this.projects = projects;
    }
    
    public List<InvoiceDto> getInvoices() {
        return invoices;
    }
    
    public void setInvoices(List<InvoiceDto> invoices) {
        this.invoices = invoices;
    }
    
    public List<ProjectDocumentDto> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<ProjectDocumentDto> documents) {
        this.documents = documents;
    }
    
    // Nested DTO for statistics
    public static class ClientStatisticsDto {
        private Long totalProjects;
        private Long activeProjects;
        private Long completedProjects;
        private Long totalInvoices;
        private Long paidInvoices;
        private Long pendingInvoices;
        private Long overdueInvoices;
        private BigDecimal totalRevenue;
        private BigDecimal pendingRevenue;
        private Long totalDocuments;
        
        public ClientStatisticsDto() {}
        
        // Getters and Setters
        public Long getTotalProjects() {
            return totalProjects;
        }
        
        public void setTotalProjects(Long totalProjects) {
            this.totalProjects = totalProjects;
        }
        
        public Long getActiveProjects() {
            return activeProjects;
        }
        
        public void setActiveProjects(Long activeProjects) {
            this.activeProjects = activeProjects;
        }
        
        public Long getCompletedProjects() {
            return completedProjects;
        }
        
        public void setCompletedProjects(Long completedProjects) {
            this.completedProjects = completedProjects;
        }
        
        public Long getTotalInvoices() {
            return totalInvoices;
        }
        
        public void setTotalInvoices(Long totalInvoices) {
            this.totalInvoices = totalInvoices;
        }
        
        public Long getPaidInvoices() {
            return paidInvoices;
        }
        
        public void setPaidInvoices(Long paidInvoices) {
            this.paidInvoices = paidInvoices;
        }
        
        public Long getPendingInvoices() {
            return pendingInvoices;
        }
        
        public void setPendingInvoices(Long pendingInvoices) {
            this.pendingInvoices = pendingInvoices;
        }
        
        public Long getOverdueInvoices() {
            return overdueInvoices;
        }
        
        public void setOverdueInvoices(Long overdueInvoices) {
            this.overdueInvoices = overdueInvoices;
        }
        
        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }
        
        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
        
        public BigDecimal getPendingRevenue() {
            return pendingRevenue;
        }
        
        public void setPendingRevenue(BigDecimal pendingRevenue) {
            this.pendingRevenue = pendingRevenue;
        }
        
        public Long getTotalDocuments() {
            return totalDocuments;
        }
        
        public void setTotalDocuments(Long totalDocuments) {
            this.totalDocuments = totalDocuments;
        }
    }
}


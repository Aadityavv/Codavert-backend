package com.codavert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceDto {
    
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    private String invoiceNumber;
    
    @NotNull
    private LocalDate invoiceDate;
    
    @NotNull
    private Long clientId;
    
    @NotNull
    private Long projectId;
    
    private Long userId;
    
    @NotNull
    private LocalDate dueDate;
    
    @NotNull
    private BigDecimal subtotal;
    
    private BigDecimal taxAmount;
    
    @NotNull
    private BigDecimal totalAmount;
    
    @Size(max = 500)
    private String notes;
    
    @Size(max = 200)
    private String paymentTerms;
    
    private LocalDate paidDate;
    
    @Size(max = 50)
    private String paymentMethod;
    
    private BigDecimal amountReceived;
    
    private String status;
    
    // Constructors
    public InvoiceDto() {}
    
    public InvoiceDto(String invoiceNumber, LocalDate invoiceDate, Long clientId, Long projectId, 
                     LocalDate dueDate, BigDecimal subtotal, BigDecimal totalAmount) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.clientId = clientId;
        this.projectId = projectId;
        this.dueDate = dueDate;
        this.subtotal = subtotal;
        this.totalAmount = totalAmount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
    
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
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
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
    
    public LocalDate getPaidDate() {
        return paidDate;
    }
    
    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public BigDecimal getAmountReceived() {
        return amountReceived;
    }
    
    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

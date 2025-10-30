package com.codavert.service;

import com.codavert.dto.InvoiceDto;
import com.codavert.entity.Client;
import com.codavert.entity.Invoice;
import com.codavert.entity.Project;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.InvoiceRepository;
import com.codavert.repository.ProjectRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;

    public Page<Invoice> getInvoicesByUserId(Long userId, Pageable pageable) {
        return invoiceRepository.findByUserId(userId, pageable);
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Invoice createInvoice(InvoiceDto dto) {
        Project project = projectRepository.findById(dto.getProjectId())
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.getProjectId()));
        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + dto.getClientId()));
        User user = project.getUser();

        String nextNumber = dto.getInvoiceNumber() != null ? dto.getInvoiceNumber() : nextInvoiceNumber(user.getId());
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(nextNumber);
        invoice.setInvoiceDate(dto.getInvoiceDate() != null ? dto.getInvoiceDate() : LocalDate.now());
        invoice.setDueDate(dto.getDueDate());
        invoice.setSubtotal(dto.getSubtotal());
        invoice.setTaxAmount(dto.getTaxAmount() != null ? dto.getTaxAmount() : BigDecimal.ZERO);
        invoice.setTotalAmount(dto.getTotalAmount());
        invoice.setNotes(dto.getNotes());
        invoice.setPaymentTerms(dto.getPaymentTerms());
        invoice.setPaidDate(dto.getPaidDate());
        invoice.setPaymentMethod(dto.getPaymentMethod());
        invoice.setAmountReceived(dto.getAmountReceived() != null ? dto.getAmountReceived() : BigDecimal.ZERO);
        invoice.setStatus(dto.getStatus() != null ? Invoice.InvoiceStatus.valueOf(dto.getStatus()) : Invoice.InvoiceStatus.DRAFT);
        invoice.setProject(project);
        invoice.setClient(client);
        invoice.setUser(user);

        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, InvoiceDto dto) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));

        if (dto.getInvoiceNumber() != null) {
            invoice.setInvoiceNumber(dto.getInvoiceNumber());
        }
        if (dto.getInvoiceDate() != null) {
            invoice.setInvoiceDate(dto.getInvoiceDate());
        }
        if (dto.getSubtotal() != null) {
            invoice.setSubtotal(dto.getSubtotal());
        }
        if (dto.getTaxAmount() != null) {
            invoice.setTaxAmount(dto.getTaxAmount());
        }
        if (dto.getTotalAmount() != null) {
            invoice.setTotalAmount(dto.getTotalAmount());
        }
        if (dto.getDueDate() != null) {
            invoice.setDueDate(dto.getDueDate());
        }
        if (dto.getNotes() != null) {
            invoice.setNotes(dto.getNotes());
        }
        if (dto.getPaymentTerms() != null) {
            invoice.setPaymentTerms(dto.getPaymentTerms());
        }
        if (dto.getPaidDate() != null) {
            invoice.setPaidDate(dto.getPaidDate());
        }
        if (dto.getPaymentMethod() != null) {
            invoice.setPaymentMethod(dto.getPaymentMethod());
        }
        if (dto.getAmountReceived() != null) {
            invoice.setAmountReceived(dto.getAmountReceived());
        }
        if (dto.getStatus() != null) {
            invoice.setStatus(Invoice.InvoiceStatus.valueOf(dto.getStatus()));
        }

        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
    }

    public void markSent(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        invoice.setStatus(Invoice.InvoiceStatus.SENT);
        invoiceRepository.save(invoice);
    }

    public String getNextInvoiceNumber(Long userId) {
        String max = invoiceRepository.findMaxInvoiceNumberByUserId(userId);
        int next = 1;
        if (max != null && max.matches("INV-\\d+")) {
            next = Integer.parseInt(max.substring(4)) + 1;
        }
        return String.format("INV-%04d", next);
    }
    
    private String nextInvoiceNumber(Long userId) {
        return getNextInvoiceNumber(userId);
    }

    public List<Invoice> getInvoicesByProjectId(Long projectId) {
        return invoiceRepository.findByProjectId(projectId);
    }
}



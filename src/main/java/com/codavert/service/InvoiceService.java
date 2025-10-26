package com.codavert.service;

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
import java.util.Optional;

@Service
public class InvoiceService {

    public static class InvoiceDto {
        public Long clientId;
        public Long projectId;
        public Double amount;
        public String dueDate; // ISO date
        public String description;
    }

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
        Project project = projectRepository.findById(dto.projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.projectId));
        Client client = clientRepository.findById(dto.clientId)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + dto.clientId));
        User user = project.getUser();

        String nextNumber = nextInvoiceNumber(user.getId());
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(nextNumber);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.parse(dto.dueDate));
        invoice.setSubtotal(BigDecimal.valueOf(dto.amount));
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(BigDecimal.valueOf(dto.amount));
        invoice.setNotes(dto.description);
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setProject(project);
        invoice.setClient(client);
        invoice.setUser(user);

        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, InvoiceDto dto) {
        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));

        if (dto.amount != null) {
            invoice.setSubtotal(BigDecimal.valueOf(dto.amount));
            invoice.setTotalAmount(BigDecimal.valueOf(dto.amount));
        }
        if (dto.dueDate != null) {
            invoice.setDueDate(LocalDate.parse(dto.dueDate));
        }
        if (dto.description != null) {
            invoice.setNotes(dto.description);
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

    private String nextInvoiceNumber(Long userId) {
        String max = invoiceRepository.findMaxInvoiceNumberByUserId(userId);
        int next = 1;
        if (max != null && max.matches("INV-\\d+")) {
            next = Integer.parseInt(max.substring(4)) + 1;
        }
        return String.format("INV-%04d", next);
    }
}



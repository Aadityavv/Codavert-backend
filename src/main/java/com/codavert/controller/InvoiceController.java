package com.codavert.controller;

import com.codavert.dto.InvoiceDto;
import com.codavert.entity.Invoice;
import com.codavert.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<Page<Invoice>> getInvoices(@RequestParam Long userId, Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceDto dto) {
        return ResponseEntity.ok(invoiceService.createInvoice(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDto dto) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted successfully");
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<?> sendInvoice(@PathVariable Long id) {
        invoiceService.markSent(id);
        return ResponseEntity.ok("Invoice sent");
    }

    @GetMapping("/next-number")
    public ResponseEntity<String> getNextInvoiceNumber(@RequestParam Long userId) {
        return ResponseEntity.ok(invoiceService.getNextInvoiceNumber(userId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Invoice>> getInvoicesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByProjectId(projectId));
    }
}



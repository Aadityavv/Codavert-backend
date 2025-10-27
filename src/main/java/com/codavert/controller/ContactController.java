package com.codavert.controller;

import com.codavert.dto.ContactFormDto;
import com.codavert.entity.ContactSubmission;
import com.codavert.repository.ContactSubmissionRepository;
import com.codavert.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Contact", description = "Contact form endpoints")
public class ContactController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;
    
    @PostMapping("/submit")
    @Operation(summary = "Submit contact form", description = "Submit a contact form and send notification email")
    public ResponseEntity<Map<String, Object>> submitContactForm(@Valid @RequestBody ContactFormDto contactForm) {
        logger.info("📨 Received contact form submission from: {}", contactForm.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        
        // Save to database
        ContactSubmission submission = new ContactSubmission(
            contactForm.getFullName(),
            contactForm.getEmail(),
            contactForm.getCompany(),
            contactForm.getMessage()
        );
        ContactSubmission savedSubmission = contactSubmissionRepository.save(submission);
        
        logger.info("📝 Contact form saved - ID: {}, Name: {}, Email: {}, Company: {}", 
                    savedSubmission.getId(),
                    contactForm.getFullName(), 
                    contactForm.getEmail(), 
                    contactForm.getCompany());
        
        // Send email notification asynchronously (runs in background, doesn't block response)
        emailService.sendContactFormEmail(contactForm);
        logger.info("📧 Email notification queued for: {}", contactForm.getFullName());
        
        // Return success immediately - don't wait for email to send
        response.put("success", true);
        response.put("message", "Thank you for contacting us! We'll get back to you shortly.");
        response.put("submissionId", savedSubmission.getId());
        
        logger.info("✅ Contact form processed successfully for: {}", contactForm.getFullName());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/submissions")
    @Operation(summary = "Get all contact submissions", description = "Get paginated list of contact form submissions")
    public ResponseEntity<Page<ContactSubmission>> getSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactSubmission> submissions = contactSubmissionRepository.findAllByOrderByCreatedAtDesc(pageable);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/submissions/{id}")
    @Operation(summary = "Get contact submission by ID")
    public ResponseEntity<ContactSubmission> getSubmission(@PathVariable Long id) {
        return contactSubmissionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/submissions/{id}/status")
    @Operation(summary = "Update submission status")
    public ResponseEntity<ContactSubmission> updateSubmissionStatus(
            @PathVariable Long id,
            @RequestParam ContactSubmission.SubmissionStatus status) {
        return contactSubmissionRepository.findById(id)
                .map(submission -> {
                    submission.setStatus(status);
                    if (status == ContactSubmission.SubmissionStatus.RESPONDED) {
                        submission.setRespondedAt(LocalDateTime.now());
                    }
                    return ResponseEntity.ok(contactSubmissionRepository.save(submission));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/submissions/{id}/notes")
    @Operation(summary = "Update submission notes")
    public ResponseEntity<ContactSubmission> updateSubmissionNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return contactSubmissionRepository.findById(id)
                .map(submission -> {
                    submission.setNotes(body.get("notes"));
                    return ResponseEntity.ok(contactSubmissionRepository.save(submission));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/submissions/{id}")
    @Operation(summary = "Delete submission")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        if (contactSubmissionRepository.existsById(id)) {
            contactSubmissionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/submissions/stats")
    @Operation(summary = "Get submission statistics")
    public ResponseEntity<Map<String, Long>> getSubmissionStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("new", contactSubmissionRepository.countByStatus(ContactSubmission.SubmissionStatus.NEW));
        stats.put("viewed", contactSubmissionRepository.countByStatus(ContactSubmission.SubmissionStatus.VIEWED));
        stats.put("responded", contactSubmissionRepository.countByStatus(ContactSubmission.SubmissionStatus.RESPONDED));
        stats.put("archived", contactSubmissionRepository.countByStatus(ContactSubmission.SubmissionStatus.ARCHIVED));
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if contact service is available")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Contact Form Service");
        return ResponseEntity.ok(response);
    }
}


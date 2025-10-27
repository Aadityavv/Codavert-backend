package com.codavert.controller;

import com.codavert.dto.ContactFormDto;
import com.codavert.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    @PostMapping("/submit")
    @Operation(summary = "Submit contact form", description = "Submit a contact form and send notification email")
    public ResponseEntity<Map<String, Object>> submitContactForm(@Valid @RequestBody ContactFormDto contactForm) {
        logger.info("📨 Received contact form submission from: {}", contactForm.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        
        // Log the contact form submission (could be saved to database here)
        logger.info("📝 Contact form data - Name: {}, Email: {}, Company: {}, Message: {}", 
                    contactForm.getFullName(), 
                    contactForm.getEmail(), 
                    contactForm.getCompany(), 
                    contactForm.getMessage());
        
        // Send email notification asynchronously (runs in background, doesn't block response)
        emailService.sendContactFormEmail(contactForm);
        logger.info("📧 Email notification queued for: {}", contactForm.getFullName());
        
        // Return success immediately - don't wait for email to send
        response.put("success", true);
        response.put("message", "Thank you for contacting us! We'll get back to you shortly.");
        
        logger.info("✅ Contact form processed successfully for: {}", contactForm.getFullName());
        return ResponseEntity.ok(response);
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


package com.codavert.controller;

import com.codavert.dto.JobApplicationDto;
import com.codavert.entity.JobApplication;
import com.codavert.service.JobApplicationService;
import com.codavert.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job-applications")
@Tag(name = "Job Applications", description = "Job Application Management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobApplicationController {
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping
    @Operation(summary = "Submit a new job application (Public)")
    public ResponseEntity<?> createApplication(@RequestBody JobApplicationDto dto) {
        try {
            JobApplication application = jobApplicationService.createApplication(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error creating application: " + e.getMessage());
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all job applications (Admin only)")
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        List<JobApplication> applications = jobApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get job application by ID (Admin only)")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            JobApplication application = jobApplicationService.getApplicationById(id);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Application not found: " + e.getMessage());
        }
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get applications by status (Admin only)")
    public ResponseEntity<List<JobApplication>> getApplicationsByStatus(
            @PathVariable JobApplication.ApplicationStatus status) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update application status (Admin only)")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam JobApplication.ApplicationStatus status,
            @RequestParam(required = false) String adminNotes) {
        try {
            JobApplication updated = jobApplicationService.updateApplicationStatus(id, status, adminNotes);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error updating status: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/hire")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Hire an applicant (Admin only)")
    public ResponseEntity<?> hireApplicant(
            @PathVariable Long id,
            @RequestBody JobApplicationDto hireDetails) {
        try {
            JobApplication hired = jobApplicationService.hireApplicant(id, hireDetails);
            return ResponseEntity.ok(hired);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error hiring applicant: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update job application (Admin only)")
    public ResponseEntity<?> updateApplication(
            @PathVariable Long id,
            @RequestBody JobApplicationDto dto) {
        try {
            JobApplication updated = jobApplicationService.updateApplication(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error updating application: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete job application (Admin only)")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        try {
            jobApplicationService.deleteApplication(id);
            return ResponseEntity.ok("Application deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error deleting application: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/send-offer-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send offer letter email to candidate (Admin only)")
    public ResponseEntity<?> sendOfferLetterEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            JobApplication application = jobApplicationService.getApplicationById(id);
            if (application.getStatus() != JobApplication.ApplicationStatus.HIRED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Application must be in HIRED status to send offer letter");
            }
            
            String offerDetails = request.getOrDefault("offerDetails", "");
            String pdfBase64 = request.getOrDefault("pdfBase64", null);
            
            emailService.sendOfferLetterEmail(
                application.getEmail(),
                application.getFullName(),
                application.getAssignedRole() != null ? application.getAssignedRole() : application.getPosition(),
                offerDetails,
                pdfBase64
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Offer letter email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error sending offer letter email: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/send-interview-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send interview invitation email with Google Meet link (Admin only)")
    public ResponseEntity<?> sendInterviewInvitationEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            JobApplication application = jobApplicationService.getApplicationById(id);
            
            String interviewDate = request.get("interviewDate");
            String interviewTime = request.get("interviewTime");
            String meetLink = request.get("meetLink");
            String notes = request.getOrDefault("notes", "");
            
            if (interviewDate == null || interviewTime == null || meetLink == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing required fields: interviewDate, interviewTime, meetLink");
            }
            
            emailService.sendInterviewInvitationEmail(
                application.getEmail(),
                application.getFullName(),
                application.getPosition(),
                interviewDate,
                interviewTime,
                meetLink,
                notes
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Interview invitation email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error sending interview invitation email: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/send-rejection-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send rejection email to candidate (Admin only)")
    public ResponseEntity<?> sendRejectionEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            JobApplication application = jobApplicationService.getApplicationById(id);
            
            String notes = request.getOrDefault("notes", "");
            
            emailService.sendRejectionEmail(
                application.getEmail(),
                application.getFullName(),
                application.getPosition(),
                notes
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Rejection email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error sending rejection email: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/accept-offer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Accept offer and create employee account (Admin only)")
    public ResponseEntity<?> acceptOfferAndCreateStaffAccount(@PathVariable Long id) {
        try {
            com.codavert.entity.User employeeUser = jobApplicationService.acceptOfferAndCreateStaffAccount(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Offer accepted and employee account created successfully");
            response.put("employeeUser", employeeUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error accepting offer: " + e.getMessage());
        }
    }
}







package com.codavert.controller;

import com.codavert.dto.JobApplicationDto;
import com.codavert.entity.JobApplication;
import com.codavert.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
@Tag(name = "Job Applications", description = "Job Application Management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobApplicationController {
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
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
}


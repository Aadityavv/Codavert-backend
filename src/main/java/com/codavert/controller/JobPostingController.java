package com.codavert.controller;

import com.codavert.dto.JobPostingDto;
import com.codavert.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-postings")
@Tag(name = "Job Postings", description = "Job Posting Management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobPostingController {
    
    @Autowired
    private JobPostingService jobPostingService;
    
    @GetMapping
    @Operation(summary = "Get all job postings (Public - returns active only)")
    public ResponseEntity<List<JobPostingDto>> getAllJobPostings(
            @RequestParam(required = false) Boolean activeOnly) {
        List<JobPostingDto> postings;
        if (activeOnly != null && activeOnly) {
            postings = jobPostingService.getActiveJobPostings();
        } else {
            postings = jobPostingService.getAllJobPostings();
        }
        return ResponseEntity.ok(postings);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active job postings (Public)")
    public ResponseEntity<List<JobPostingDto>> getActiveJobPostings() {
        List<JobPostingDto> postings = jobPostingService.getActiveJobPostings();
        return ResponseEntity.ok(postings);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get job posting by ID (Public)")
    public ResponseEntity<?> getJobPostingById(@PathVariable Long id) {
        try {
            JobPostingDto posting = jobPostingService.getJobPostingById(id);
            return ResponseEntity.ok(posting);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Job posting not found: " + e.getMessage());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new job posting (Admin only)")
    public ResponseEntity<?> createJobPosting(@RequestBody JobPostingDto dto) {
        try {
            JobPostingDto created = jobPostingService.createJobPosting(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error creating job posting: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update job posting (Admin only)")
    public ResponseEntity<?> updateJobPosting(
            @PathVariable Long id,
            @RequestBody JobPostingDto dto) {
        try {
            JobPostingDto updated = jobPostingService.updateJobPosting(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error updating job posting: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete job posting (Admin only)")
    public ResponseEntity<?> deleteJobPosting(@PathVariable Long id) {
        try {
            jobPostingService.deleteJobPosting(id);
            return ResponseEntity.ok("Job posting deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error deleting job posting: " + e.getMessage());
        }
    }
}


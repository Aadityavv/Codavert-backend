package com.codavert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String position; // The position they're applying for
    
    @Column(length = 2000)
    private String coverLetter;
    
    @Column(length = 500)
    private String resumeUrl; // URL to uploaded resume (if stored)
    
    private String portfolioUrl;
    
    private String linkedinUrl;
    
    private String githubUrl;
    
    private Integer yearsOfExperience;
    
    @Column(length = 1000)
    private String skills; // Comma-separated skills
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.NEW;
    
    @Column(length = 1000)
    private String adminNotes;
    
    // Fields for hired applicants
    private String assignedRole; // The actual role assigned (SDE, Designer, Intern, etc.)
    private Double stipend;
    private String joiningDate;
    private String employmentType; // Full-time, Part-time, Internship, Contract
    private String department;
    private String workLocation; // Remote, Office, Hybrid
    private Boolean offerAccepted = false; // Whether the offer has been accepted
    private Long staffUserId; // Reference to created staff user account
    
    @Column(nullable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();
    
    private LocalDateTime reviewedAt;
    
    private LocalDateTime hiredAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ApplicationStatus {
        NEW,           // Just submitted
        REVIEWING,     // Under review
        SHORTLISTED,   // Shortlisted for interview
        INTERVIEWED,   // Interview completed
        HIRED,         // Accepted and hired
        OFFER_ACCEPTED, // Offer accepted by candidate
        REJECTED,      // Application rejected
        WITHDRAWN      // Applicant withdrew
    }
}






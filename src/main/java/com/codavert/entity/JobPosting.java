package com.codavert.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPosting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobType type = JobType.FULL_TIME;
    
    @Column(nullable = false, length = 100)
    private String location;
    
    @Column(length = 50)
    private String experience; // e.g., "3+ years", "5+ years"
    
    @Column(length = 100)
    private String salary; // e.g., "$60k - $90k"
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "job_posting_requirements", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "requirement", length = 500)
    private List<String> requirements = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "job_posting_responsibilities", joinColumns = @JoinColumn(name = "job_posting_id"))
    @Column(name = "responsibility", length = 500)
    private List<String> responsibilities = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostingStatus status = PostingStatus.ACTIVE;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum JobType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP,
        FREELANCE
    }
    
    public enum PostingStatus {
        ACTIVE,
        INACTIVE,
        CLOSED,
        DRAFT
    }
}




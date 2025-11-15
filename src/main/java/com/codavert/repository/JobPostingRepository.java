package com.codavert.repository;

import com.codavert.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    List<JobPosting> findByStatus(JobPosting.PostingStatus status);
    List<JobPosting> findByStatusOrderByCreatedAtDesc(JobPosting.PostingStatus status);
    List<JobPosting> findByType(JobPosting.JobType type);
    List<JobPosting> findByLocationContainingIgnoreCase(String location);
}


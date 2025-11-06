package com.codavert.repository;

import com.codavert.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    List<JobApplication> findByStatus(JobApplication.ApplicationStatus status);
    
    List<JobApplication> findByPosition(String position);
    
    Optional<JobApplication> findByEmail(String email);
    
    List<JobApplication> findByOrderByAppliedAtDesc();
    
    List<JobApplication> findByStatusOrderByAppliedAtDesc(JobApplication.ApplicationStatus status);
}


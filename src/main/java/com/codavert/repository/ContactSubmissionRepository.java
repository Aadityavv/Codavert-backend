package com.codavert.repository;

import com.codavert.entity.ContactSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {
    
    Page<ContactSubmission> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<ContactSubmission> findByStatus(ContactSubmission.SubmissionStatus status);
    
    long countByStatus(ContactSubmission.SubmissionStatus status);
}


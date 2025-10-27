package com.codavert.repository;

import com.codavert.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<ActivityLog> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<ActivityLog> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, LocalDateTime after);
    
    Page<ActivityLog> findByUserIdAndEntityTypeOrderByCreatedAtDesc(Long userId, ActivityLog.EntityType entityType, Pageable pageable);
    
    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);
}


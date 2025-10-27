package com.codavert.repository;

import com.codavert.entity.TimeEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    Page<TimeEntry> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);
    
    Page<TimeEntry> findByProjectIdOrderByStartTimeDesc(Long projectId, Pageable pageable);
    
    Page<TimeEntry> findByTaskIdOrderByStartTimeDesc(Long taskId, Pageable pageable);
    
    Page<TimeEntry> findByUserIdAndProjectIdOrderByStartTimeDesc(Long userId, Long projectId, Pageable pageable);
    
    List<TimeEntry> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    List<TimeEntry> findByProjectIdAndStartTimeBetween(Long projectId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(t.hoursLogged) FROM TimeEntry t WHERE t.projectId = ?1 AND t.status = 'APPROVED'")
    Double sumApprovedHoursByProjectId(Long projectId);
    
    @Query("SELECT SUM(t.hoursLogged) FROM TimeEntry t WHERE t.taskId = ?1 AND t.status = 'APPROVED'")
    Double sumApprovedHoursByTaskId(Long taskId);
    
    @Query("SELECT SUM(t.hoursLogged) FROM TimeEntry t WHERE t.userId = ?1 AND t.startTime >= ?2 AND t.startTime < ?3")
    Double sumHoursByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);
    
    long countByUserIdAndStatus(Long userId, TimeEntry.EntryStatus status);
    
    long countByProjectIdAndStatus(Long projectId, TimeEntry.EntryStatus status);
}

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
    
    Page<TimeEntry> findByUser_IdOrderByStartTimeDesc(Long userId, Pageable pageable);
    
    Page<TimeEntry> findByProject_IdOrderByStartTimeDesc(Long projectId, Pageable pageable);
    
    Page<TimeEntry> findByTask_IdOrderByStartTimeDesc(Long taskId, Pageable pageable);
    
    Page<TimeEntry> findByUser_IdAndProject_IdOrderByStartTimeDesc(Long userId, Long projectId, Pageable pageable);
    
    List<TimeEntry> findByUser_IdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    List<TimeEntry> findByProject_IdAndStartTimeBetween(Long projectId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(t.hoursLogged) FROM TimeEntry t WHERE t.project.id = ?1 AND t.status = 'APPROVED'")
    Double sumApprovedHoursByProjectId(Long projectId);
    
    @Query("SELECT SUM(t.hoursLogged) FROM TimeEntry t WHERE t.task.id = ?1 AND t.status = 'APPROVED'")
    Double sumApprovedHoursByTaskId(Long taskId);
    
    @Query("SELECT SUM(t.hoursLogged) FROM TimeEntry t WHERE t.user.id = ?1 AND t.startTime >= ?2 AND t.startTime < ?3")
    Double sumHoursByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);
    
    long countByUser_IdAndStatus(Long userId, TimeEntry.EntryStatus status);
    
    long countByProject_IdAndStatus(Long projectId, TimeEntry.EntryStatus status);
}

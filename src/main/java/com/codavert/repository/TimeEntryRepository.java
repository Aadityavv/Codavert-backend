package com.codavert.repository;

import com.codavert.entity.TimeEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    List<TimeEntry> findByUserId(Long userId);
    
    Page<TimeEntry> findByUserId(Long userId, Pageable pageable);
    
    List<TimeEntry> findByProjectId(Long projectId);
    
    Page<TimeEntry> findByProjectId(Long projectId, Pageable pageable);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.entryDate BETWEEN :startDate AND :endDate")
    List<TimeEntry> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.project.id = :projectId AND te.entryDate BETWEEN :startDate AND :endDate")
    List<TimeEntry> findByProjectIdAndDateRange(@Param("projectId") Long projectId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.status = :status")
    Page<TimeEntry> findByUserIdAndStatus(@Param("userId") Long userId, 
                                         @Param("status") TimeEntry.TimeEntryStatus status, 
                                         Pageable pageable);
    
    @Query("SELECT SUM(te.hoursWorked) FROM TimeEntry te WHERE te.user.id = :userId AND te.entryDate BETWEEN :startDate AND :endDate")
    Double sumHoursByUserIdAndDateRange(@Param("userId") Long userId, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(te.hoursWorked) FROM TimeEntry te WHERE te.project.id = :projectId")
    Double sumHoursByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT SUM(te.totalAmount) FROM TimeEntry te WHERE te.user.id = :userId AND te.entryDate BETWEEN :startDate AND :endDate")
    Double sumAmountByUserIdAndDateRange(@Param("userId") Long userId, 
                                        @Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(te) FROM TimeEntry te WHERE te.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(te) FROM TimeEntry te WHERE te.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);
}

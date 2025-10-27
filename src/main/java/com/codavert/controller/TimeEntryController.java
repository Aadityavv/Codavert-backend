package com.codavert.controller;

import com.codavert.dto.TimeEntryDto;
import com.codavert.entity.TimeEntry;
import com.codavert.repository.TimeEntryRepository;
import com.codavert.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/time-entries")
@Tag(name = "Time Entries", description = "Time tracking operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TimeEntryController {

    @Autowired
    private TimeEntryRepository timeEntryRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "Get all time entries for a user")
    public ResponseEntity<Page<TimeEntryDto>> getUserTimeEntries(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeEntry> entries = timeEntryRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);
        Page<TimeEntryDto> dtos = entries.map(TimeEntryDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get time entry by ID")
    public ResponseEntity<TimeEntryDto> getTimeEntry(@PathVariable Long id) {
        return timeEntryRepository.findById(id)
                .map(TimeEntryDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get time entries for a project")
    public ResponseEntity<Page<TimeEntryDto>> getProjectTimeEntries(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeEntry> entries = timeEntryRepository.findByProjectIdOrderByStartTimeDesc(projectId, pageable);
        Page<TimeEntryDto> dtos = entries.map(TimeEntryDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get time entries for a task")
    public ResponseEntity<Page<TimeEntryDto>> getTaskTimeEntries(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeEntry> entries = timeEntryRepository.findByTaskIdOrderByStartTimeDesc(taskId, pageable);
        Page<TimeEntryDto> dtos = entries.map(TimeEntryDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(summary = "Create a new time entry")
    public ResponseEntity<TimeEntryDto> createTimeEntry(
            @Valid @RequestBody TimeEntryDto entryDto,
            @RequestParam(required = false) Long userId) {
        
        TimeEntry entry = new TimeEntry();
        entry.setUserId(entryDto.getUserId());
        entry.setProjectId(entryDto.getProjectId());
        entry.setTaskId(entryDto.getTaskId());
        entry.setDescription(entryDto.getDescription());
        entry.setStartTime(entryDto.getStartTime());
        entry.setEndTime(entryDto.getEndTime());
        entry.setIsBillable(entryDto.getIsBillable());
        entry.setStatus(entryDto.getStatus() != null ? entryDto.getStatus() : TimeEntry.EntryStatus.DRAFT);
        entry.setNotes(entryDto.getNotes());
        
        TimeEntry savedEntry = timeEntryRepository.save(entry);
        
        // Log activity
        if (userId != null) {
            activityLogService.logActivity(userId, 
                com.codavert.entity.ActivityLog.ActivityType.CREATED,
                com.codavert.entity.ActivityLog.EntityType.TIME_ENTRY,
                savedEntry.getId(),
                "Logged " + savedEntry.getHoursLogged() + " hours: " + savedEntry.getDescription());
        }
        
        return ResponseEntity.ok(TimeEntryDto.fromEntity(savedEntry));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a time entry")
    public ResponseEntity<TimeEntryDto> updateTimeEntry(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody TimeEntryDto entryDto) {
        
        return timeEntryRepository.findById(id)
                .map(entry -> {
                    if (entryDto.getDescription() != null) entry.setDescription(entryDto.getDescription());
                    if (entryDto.getStartTime() != null) entry.setStartTime(entryDto.getStartTime());
                    if (entryDto.getEndTime() != null) entry.setEndTime(entryDto.getEndTime());
                    if (entryDto.getIsBillable() != null) entry.setIsBillable(entryDto.getIsBillable());
                    if (entryDto.getStatus() != null) entry.setStatus(entryDto.getStatus());
                    if (entryDto.getNotes() != null) entry.setNotes(entryDto.getNotes());
                    
                    TimeEntry updatedEntry = timeEntryRepository.save(entry);
                    
                    // Log activity
                    if (userId != null) {
                        activityLogService.logActivity(userId,
                            com.codavert.entity.ActivityLog.ActivityType.UPDATED,
                            com.codavert.entity.ActivityLog.EntityType.TIME_ENTRY,
                            updatedEntry.getId(),
                            "Updated time entry: " + updatedEntry.getDescription());
                    }
                    
                    return ResponseEntity.ok(TimeEntryDto.fromEntity(updatedEntry));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a time entry")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable Long id) {
        if (timeEntryRepository.existsById(id)) {
            timeEntryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/statistics/project/{projectId}")
    @Operation(summary = "Get time statistics for a project")
    public ResponseEntity<Map<String, Object>> getProjectTimeStatistics(@PathVariable Long projectId) {
        Map<String, Object> stats = new HashMap<>();
        
        Double totalApprovedHours = timeEntryRepository.sumApprovedHoursByProjectId(projectId);
        long draftEntries = timeEntryRepository.countByProjectIdAndStatus(projectId, TimeEntry.EntryStatus.DRAFT);
        long submittedEntries = timeEntryRepository.countByProjectIdAndStatus(projectId, TimeEntry.EntryStatus.SUBMITTED);
        long approvedEntries = timeEntryRepository.countByProjectIdAndStatus(projectId, TimeEntry.EntryStatus.APPROVED);
        
        stats.put("totalApprovedHours", totalApprovedHours != null ? totalApprovedHours : 0.0);
        stats.put("draftEntries", draftEntries);
        stats.put("submittedEntries", submittedEntries);
        stats.put("approvedEntries", approvedEntries);
        stats.put("totalEntries", draftEntries + submittedEntries + approvedEntries);
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/task/{taskId}")
    @Operation(summary = "Get time statistics for a task")
    public ResponseEntity<Map<String, Object>> getTaskTimeStatistics(@PathVariable Long taskId) {
        Map<String, Object> stats = new HashMap<>();
        
        Double totalApprovedHours = timeEntryRepository.sumApprovedHoursByTaskId(taskId);
        stats.put("totalApprovedHours", totalApprovedHours != null ? totalApprovedHours : 0.0);
        
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update time entry status")
    public ResponseEntity<TimeEntryDto> updateTimeEntryStatus(
            @PathVariable Long id,
            @RequestParam TimeEntry.EntryStatus status) {
        
        return timeEntryRepository.findById(id)
                .map(entry -> {
                    entry.setStatus(status);
                    TimeEntry updatedEntry = timeEntryRepository.save(entry);
                    return ResponseEntity.ok(TimeEntryDto.fromEntity(updatedEntry));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


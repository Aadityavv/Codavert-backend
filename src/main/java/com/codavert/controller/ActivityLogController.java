package com.codavert.controller;

import com.codavert.dto.ActivityLogDto;
import com.codavert.entity.ActivityLog;
import com.codavert.security.UserPrincipal;
import com.codavert.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<Page<ActivityLogDto>> getUserActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ActivityLog> activities = activityLogService.getUserActivities(userPrincipal.getId(), page, size);
        Page<ActivityLogDto> dtos = activities.map(ActivityLogDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLogDto>> getRecentActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<ActivityLog> activities = activityLogService.getRecentActivities(userPrincipal.getId(), limit);
        List<ActivityLogDto> dtos = activities.stream()
                .map(ActivityLogDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/by-type/{entityType}")
    public ResponseEntity<Page<ActivityLogDto>> getActivitiesByType(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable ActivityLog.EntityType entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ActivityLog> activities = activityLogService.getActivitiesByEntityType(
                userPrincipal.getId(), entityType, page, size);
        Page<ActivityLogDto> dtos = activities.map(ActivityLogDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/count/recent")
    public ResponseEntity<Long> countRecentActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "24") int hours) {
        
        long count = activityLogService.countRecentActivities(userPrincipal.getId(), hours);
        return ResponseEntity.ok(count);
    }
}

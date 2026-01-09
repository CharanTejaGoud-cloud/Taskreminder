package com.example.taskreminder.controller;

import com.example.taskreminder.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for reminder information.
 */
@RestController
@RequestMapping("/reminders")
public class ReminderController {

    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);

    private final ScheduleService scheduleService;

    @Autowired
    public ReminderController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * GET /reminders/{taskId} - Get scheduled reminder info for a task
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getReminderInfo(@PathVariable Long taskId) {
        try {
            ScheduleService.ReminderInfo info = scheduleService.getReminderInfo(taskId);
            
            if (info == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No scheduled reminder found for task ID: " + taskId));
            }
            
            return ResponseEntity.ok(Map.of(
                "taskId", info.getTaskId(),
                "scheduledTime", info.getScheduledTime(),
                "timezone", info.getTimezone() != null ? info.getTimezone() : "system default"
            ));
        } catch (Exception e) {
            logger.error("Error getting reminder info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get reminder info: " + e.getMessage()));
        }
    }
}


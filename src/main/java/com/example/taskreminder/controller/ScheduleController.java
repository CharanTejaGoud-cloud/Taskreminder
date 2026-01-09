package com.example.taskreminder.controller;

import com.example.taskreminder.model.Task;
import com.example.taskreminder.service.ScheduleService;
import com.example.taskreminder.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST controller for scheduling operations.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    private final ScheduleService scheduleService;
    private final TaskService taskService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService, TaskService taskService) {
        this.scheduleService = scheduleService;
        this.taskService = taskService;
    }

    /**
     * POST /schedule/set - Set schedule/reminder for a task
     */
    @PostMapping("/set")
    public ResponseEntity<?> setSchedule(@RequestBody Map<String, Object> request) {
        try {
            Object taskIdObj = request.get("taskId");
            if (taskIdObj == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "taskId is required"));
            }
            
            Long taskId;
            if (taskIdObj instanceof Number) {
                taskId = ((Number) taskIdObj).longValue();
            } else {
                taskId = Long.parseLong(taskIdObj.toString());
            }
            
            Optional<Task> taskOpt = taskService.getTaskById(taskId);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task not found with ID: " + taskId));
            }
            
            Task task = taskOpt.get();
            String timezone = (String) request.get("timezone");
            
            if (timezone != null && !timezone.isEmpty()) {
                scheduleService.scheduleReminder(task, timezone);
            } else {
                scheduleService.scheduleReminder(task);
            }
            
            logger.info("Scheduled reminder for task ID: {}", taskId);
            return ResponseEntity.ok(Map.of(
                "message", "Reminder scheduled successfully",
                "taskId", taskId
            ));
        } catch (Exception e) {
            logger.error("Error setting schedule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to set schedule: " + e.getMessage()));
        }
    }
}


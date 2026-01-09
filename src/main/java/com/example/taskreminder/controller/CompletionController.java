package com.example.taskreminder.controller;

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
 * REST controller for task completion operations.
 */
@RestController
@RequestMapping("/completion")
public class CompletionController {

    private static final Logger logger = LoggerFactory.getLogger(CompletionController.class);

    private final TaskService taskService;
    private final ScheduleService scheduleService;

    @Autowired
    public CompletionController(TaskService taskService, ScheduleService scheduleService) {
        this.taskService = taskService;
        this.scheduleService = scheduleService;
    }

    /**
     * PUT /completion/mark - Mark a task as completed
     */
    @PutMapping("/mark")
    public ResponseEntity<?> markTaskCompleted(@RequestBody Map<String, Object> request) {
        try {
            Object idObj = request.get("id");
            if (idObj == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Task id is required"));
            }
            
            Long id;
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else {
                id = Long.parseLong(idObj.toString());
            }
            
            Optional<String> statusOpt = taskService.getTaskStatus(id);
            if (statusOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task not found with ID: " + id));
            }
            
            boolean completed = taskService.markTaskCompleted(id);
            
            if (completed) {
                // Cancel any scheduled reminder
                scheduleService.cancelReminder(id);
                
                logger.info("Marked task {} as completed", id);
                return ResponseEntity.ok(Map.of(
                    "message", "Task marked as completed",
                    "taskId", id
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark task as completed"));
            }
        } catch (Exception e) {
            logger.error("Error marking task as completed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to mark task as completed: " + e.getMessage()));
        }
    }
}


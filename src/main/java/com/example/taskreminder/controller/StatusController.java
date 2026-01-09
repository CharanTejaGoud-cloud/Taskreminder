package com.example.taskreminder.controller;

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
 * REST controller for task status operations.
 */
@RestController
@RequestMapping("/status")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final TaskService taskService;

    @Autowired
    public StatusController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET /status/{taskId} - Get task status
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskStatus(@PathVariable Long taskId) {
        try {
            Optional<String> statusOpt = taskService.getTaskStatus(taskId);
            
            if (statusOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task not found with ID: " + taskId));
            }
            
            return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", statusOpt.get()
            ));
        } catch (Exception e) {
            logger.error("Error getting task status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get task status: " + e.getMessage()));
        }
    }
}


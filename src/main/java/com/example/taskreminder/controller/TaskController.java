package com.example.taskreminder.controller;

import com.example.taskreminder.model.Task;
import com.example.taskreminder.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for task management operations.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /tasks/add - Create a new task
     */
    @PostMapping("/add")
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Task title is required"));
            }
            
            Task createdTask = taskService.createTask(task);
            logger.info("Created task with ID: {}", createdTask.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            logger.error("Error creating task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create task: " + e.getMessage()));
        }
    }

    /**
     * GET /tasks/list - List all tasks, optionally filtered by status
     */
    @GetMapping("/list")
    public ResponseEntity<?> listTasks(@RequestParam(required = false) String status) {
        try {
            List<Task> tasks = taskService.getAllTasks(status);
            logger.info("Retrieved {} tasks", tasks.size());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error listing tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to list tasks: " + e.getMessage()));
        }
    }

    /**
     * PUT /tasks/{id} - Update an existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            Optional<Task> existingTask = taskService.getTaskById(id);
            if (existingTask.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task not found with ID: " + id));
            }
            
            task.setId(id);
            boolean updated = taskService.updateTask(task);
            
            if (updated) {
                logger.info("Updated task with ID: {}", id);
                return ResponseEntity.ok(taskService.getTaskById(id).get());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update task"));
            }
        } catch (Exception e) {
            logger.error("Error updating task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update task: " + e.getMessage()));
        }
    }

    /**
     * DELETE /tasks/{id} - Delete a task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            Optional<Task> task = taskService.getTaskById(id);
            if (task.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Task not found with ID: " + id));
            }
            
            boolean deleted = taskService.deleteTask(id);
            
            if (deleted) {
                logger.info("Deleted task with ID: {}", id);
                return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete task"));
            }
        } catch (Exception e) {
            logger.error("Error deleting task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete task: " + e.getMessage()));
        }
    }
}


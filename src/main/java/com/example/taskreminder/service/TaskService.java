package com.example.taskreminder.service;

import com.example.taskreminder.model.Task;
import com.example.taskreminder.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing tasks.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Create a new task.
     */
    public Task createTask(Task task) {
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(System.currentTimeMillis());
        }
        
        logger.info("Creating task: {}", task.getTitle());
        return taskRepository.save(task);
    }

    /**
     * Get task by ID.
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Get all tasks, optionally filtered by status.
     */
    public List<Task> getAllTasks(String status) {
        if (status != null && !status.isEmpty()) {
            return taskRepository.findByStatus(status.toUpperCase());
        }
        return taskRepository.findAll();
    }

    /**
     * Update an existing task.
     */
    public boolean updateTask(Task task) {
        Optional<Task> existingTask = taskRepository.findById(task.getId());
        if (existingTask.isEmpty()) {
            logger.warn("Task not found for update: {}", task.getId());
            return false;
        }
        
        logger.info("Updating task ID: {}", task.getId());
        return taskRepository.update(task);
    }

    /**
     * Delete a task by ID.
     */
    public boolean deleteTask(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            logger.warn("Task not found for deletion: {}", id);
            return false;
        }
        
        logger.info("Deleting task ID: {}", id);
        return taskRepository.deleteById(id);
    }

    /**
     * Mark task as completed.
     */
    public boolean markTaskCompleted(Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            logger.warn("Task not found for completion: {}", id);
            return false;
        }
        
        Task task = taskOpt.get();
        if ("COMPLETED".equals(task.getStatus())) {
            logger.info("Task {} is already completed", id);
            return true;
        }
        
        task.setStatus("COMPLETED");
        task.setCompletedAt(System.currentTimeMillis());
        
        logger.info("Marking task {} as completed", id);
        return taskRepository.update(task);
    }

    /**
     * Get task status.
     */
    public Optional<String> getTaskStatus(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(Task::getStatus);
    }
}


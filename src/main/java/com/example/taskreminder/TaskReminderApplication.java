package com.example.taskreminder;

import com.example.taskreminder.model.Task;
import com.example.taskreminder.repository.TaskRepository;
import com.example.taskreminder.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * Main Spring Boot application class.
 * On startup, loads pending tasks and schedules reminders.
 */
@SpringBootApplication
public class TaskReminderApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TaskReminderApplication.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ScheduleService scheduleService;

    public static void main(String[] args) {
        SpringApplication.run(TaskReminderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("Application started. Loading pending tasks and scheduling reminders...");
        
        try {
            // Load all pending tasks
            List<Task> pendingTasks = taskRepository.findByStatus("PENDING");
            logger.info("Found {} pending tasks", pendingTasks.size());
            
            // Schedule reminders for tasks with future due dates
            for (Task task : pendingTasks) {
                if (task.getDueTimestamp() != null && task.getDueTimestamp() > System.currentTimeMillis()) {
                    scheduleService.scheduleReminder(task);
                    logger.info("Scheduled reminder for task ID: {}", task.getId());
                }
            }
            
            logger.info("Startup scheduling completed successfully");
        } catch (Exception e) {
            logger.error("Error during startup scheduling", e);
        }
    }
}


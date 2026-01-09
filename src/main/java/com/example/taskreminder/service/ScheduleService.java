package com.example.taskreminder.service;

import com.example.taskreminder.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service for scheduling task reminders using ScheduledExecutorService.
 */
@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private ScheduledExecutorService scheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledReminders = new ConcurrentHashMap<>();
    private final Map<Long, ReminderInfo> reminderInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        scheduler = Executors.newScheduledThreadPool(5);
        logger.info("ScheduleService initialized");
    }

    @PreDestroy
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("ScheduleService shutdown completed");
        }
    }

    /**
     * Schedule a reminder for a task.
     */
    public void scheduleReminder(Task task) {
        if (task == null || task.getDueTimestamp() == null) {
            logger.warn("Cannot schedule reminder: task or dueTimestamp is null");
            return;
        }

        Long taskId = task.getId();
        long dueTime = task.getDueTimestamp();
        long currentTime = System.currentTimeMillis();
        long delay = dueTime - currentTime;

        // Cancel existing reminder if any
        cancelReminder(taskId);

        if (delay <= 0) {
            logger.warn("Task {} due date is in the past, not scheduling reminder", taskId);
            return;
        }

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                logger.info("Reminder triggered for task ID: {}", taskId);
                emailService.sendReminderEmail(
                    task.getEmail(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueTimestamp()
                );
                scheduledReminders.remove(taskId);
                reminderInfoMap.remove(taskId);
            } catch (Exception e) {
                logger.error("Error sending reminder for task ID: {}", taskId, e);
            }
        }, delay, TimeUnit.MILLISECONDS);

        scheduledReminders.put(taskId, future);
        reminderInfoMap.put(taskId, new ReminderInfo(taskId, dueTime, null));
        
        logger.info("Scheduled reminder for task ID: {} in {} milliseconds", taskId, delay);
    }

    /**
     * Schedule a reminder with timezone/offset support.
     */
    public void scheduleReminder(Task task, String timezone) {
        if (task == null || task.getDueTimestamp() == null) {
            logger.warn("Cannot schedule reminder: task or dueTimestamp is null");
            return;
        }

        try {
            ZoneId zoneId = timezone != null && !timezone.isEmpty() 
                ? ZoneId.of(timezone) 
                : ZoneId.systemDefault();
            
            // Convert due timestamp to the specified timezone for logging
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(task.getDueTimestamp()),
                zoneId
            );
            
            logger.info("Scheduling reminder for task {} with timezone: {}", task.getId(), zoneId);
            
            // Schedule the reminder (still uses epoch millis internally)
            scheduleReminder(task);
            
            // Update reminder info with timezone
            ReminderInfo info = reminderInfoMap.get(task.getId());
            if (info != null) {
                info.setTimezone(zoneId.toString());
            }
        } catch (Exception e) {
            logger.error("Error scheduling reminder with timezone: {}", timezone, e);
            // Fallback to default scheduling
            scheduleReminder(task);
        }
    }

    /**
     * Cancel a scheduled reminder.
     */
    public void cancelReminder(Long taskId) {
        ScheduledFuture<?> future = scheduledReminders.remove(taskId);
        if (future != null) {
            future.cancel(false);
            logger.info("Cancelled reminder for task ID: {}", taskId);
        }
        reminderInfoMap.remove(taskId);
    }

    /**
     * Get reminder information for a task.
     */
    public ReminderInfo getReminderInfo(Long taskId) {
        return reminderInfoMap.get(taskId);
    }

    /**
     * Inner class to store reminder information.
     */
    public static class ReminderInfo {
        private Long taskId;
        private Long scheduledTime;
        private String timezone;

        public ReminderInfo(Long taskId, Long scheduledTime, String timezone) {
            this.taskId = taskId;
            this.scheduledTime = scheduledTime;
            this.timezone = timezone;
        }

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public Long getScheduledTime() {
            return scheduledTime;
        }

        public void setScheduledTime(Long scheduledTime) {
            this.scheduledTime = scheduledTime;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }
}


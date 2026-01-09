package com.example.taskreminder.service;

import com.example.taskreminder.model.Task;
import com.example.taskreminder.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating reports and CSV exports.
 */
@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final TaskRepository taskRepository;

    @Autowired
    public ReportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Get overview statistics.
     */
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByStatus("PENDING");
        long completedTasks = taskRepository.countByStatus("COMPLETED");
        
        overview.put("totalTasks", totalTasks);
        overview.put("pendingTasks", pendingTasks);
        overview.put("completedTasks", completedTasks);
        overview.put("generatedAt", System.currentTimeMillis());
        
        logger.info("Generated overview report: {}", overview);
        return overview;
    }

    /**
     * Export tasks to CSV file.
     * Uses core Java only (no external CSV libraries).
     */
    public String exportToCsv(String status) throws IOException {
        List<Task> tasks;
        if (status != null && !status.isEmpty()) {
            tasks = taskRepository.findByStatus(status.toUpperCase());
        } else {
            tasks = taskRepository.findAll();
        }

        // Create filename with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String filename = "tasks_export_" + timestamp + ".csv";
        
        // Create export directory if it doesn't exist
        Path exportDir = Paths.get("exports");
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }
        
        Path filePath = exportDir.resolve(filename);
        
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            // Write CSV header
            writer.append("ID,Title,Description,Due Timestamp,Email,Status,Created At,Completed At\n");
            
            // Write data rows
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Task task : tasks) {
                writer.append(escapeCsvField(String.valueOf(task.getId()))).append(",");
                writer.append(escapeCsvField(task.getTitle())).append(",");
                writer.append(escapeCsvField(task.getDescription() != null ? task.getDescription() : "")).append(",");
                writer.append(escapeCsvField(task.getDueTimestamp() != null 
                    ? dateFormatter.format(new Date(task.getDueTimestamp())) 
                    : "")).append(",");
                writer.append(escapeCsvField(task.getEmail() != null ? task.getEmail() : "")).append(",");
                writer.append(escapeCsvField(task.getStatus())).append(",");
                writer.append(escapeCsvField(task.getCreatedAt() != null 
                    ? dateFormatter.format(new Date(task.getCreatedAt())) 
                    : "")).append(",");
                writer.append(escapeCsvField(task.getCompletedAt() != null 
                    ? dateFormatter.format(new Date(task.getCompletedAt())) 
                    : "")).append("\n");
            }
            
            writer.flush();
        }
        
        logger.info("Exported {} tasks to CSV: {}", tasks.size(), filePath);
        return filePath.toString();
    }

    /**
     * Escape CSV field values (handle commas, quotes, newlines).
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // If field contains comma, quote, or newline, wrap in quotes and escape quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        
        return field;
    }
}


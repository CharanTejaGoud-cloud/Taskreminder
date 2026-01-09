package com.example.taskreminder.repository;

import com.example.taskreminder.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Task persistence using JdbcTemplate.
 * Handles all database operations for tasks.
 */
@Repository
public class TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper for Task objects.
     */
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setDueTimestamp(rs.getLong("due_timestamp"));
        task.setEmail(rs.getString("email"));
        task.setStatus(rs.getString("status"));
        task.setCreatedAt(rs.getLong("created_at"));
        Long completedAt = rs.getLong("completed_at");
        if (!rs.wasNull()) {
            task.setCompletedAt(completedAt);
        }
        return task;
    };

    /**
     * Save a new task and return the generated ID.
     */
    public Task save(Task task) {
        String sql = "INSERT INTO tasks (title, description, due_timestamp, email, status, created_at, completed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setLong(3, task.getDueTimestamp());
            ps.setString(4, task.getEmail());
            ps.setString(5, task.getStatus());
            ps.setLong(6, task.getCreatedAt());
            ps.setObject(7, task.getCompletedAt());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        task.setId(id);
        logger.info("Saved task with ID: {}", id);
        return task;
    }

    /**
     * Find task by ID.
     */
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(sql, taskRowMapper, id);
            return Optional.ofNullable(task);
        } catch (Exception e) {
            logger.debug("Task not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Find all tasks.
     */
    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, taskRowMapper);
    }

    /**
     * Find tasks by status.
     */
    public List<Task> findByStatus(String status) {
        String sql = "SELECT * FROM tasks WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, taskRowMapper, status);
    }

    /**
     * Update an existing task.
     */
    public boolean update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, due_timestamp = ?, " +
                     "email = ?, status = ?, completed_at = ? WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
                task.getTitle(),
                task.getDescription(),
                task.getDueTimestamp(),
                task.getEmail(),
                task.getStatus(),
                task.getCompletedAt(),
                task.getId());
        
        logger.info("Updated task with ID: {}, rows affected: {}", task.getId(), rowsAffected);
        return rowsAffected > 0;
    }

    /**
     * Delete task by ID.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleted task with ID: {}, rows affected: {}", id, rowsAffected);
        return rowsAffected > 0;
    }

    /**
     * Count tasks by status.
     */
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }

    /**
     * Count all tasks.
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM tasks";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}


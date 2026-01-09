# Task Reminder Application

A Spring Boot-based Task Reminder application that allows you to create, manage, and schedule reminders for tasks. The application uses H2 in-memory database with JDBC (no JPA), ScheduledExecutorService for scheduling, and Spring Boot Mail support for email notifications.

## Features

- **Task Management**: Create, update, delete, and list tasks
- **Scheduling**: Schedule reminders for tasks with due dates
- **Email Notifications**: Send reminder emails (with mock support for local testing)
- **Status Tracking**: Track task status (PENDING/COMPLETED)
- **Reports**: Generate overview statistics and export tasks to CSV
- **RESTful API**: Complete REST API for all operations

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Project Structure

```
task-reminder-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── taskreminder/
│   │   │               ├── TaskReminderApplication.java
│   │   │               ├── model/
│   │   │               │   └── Task.java
│   │   │               ├── repository/
│   │   │               │   └── TaskRepository.java
│   │   │               ├── service/
│   │   │               │   ├── TaskService.java
│   │   │               │   ├── ScheduleService.java
│   │   │               │   ├── EmailService.java
│   │   │               │   └── ReportService.java
│   │   │               └── controller/
│   │   │                   ├── TaskController.java
│   │   │                   ├── ScheduleController.java
│   │   │                   ├── ReminderController.java
│   │   │                   ├── CompletionController.java
│   │   │                   ├── StatusController.java
│   │   │                   └── ReportController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test/
└── pom.xml
```

## Building and Running

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access H2 Console

Navigate to `http://localhost:8080/h2-console` to access the H2 database console:
- JDBC URL: `jdbc:h2:mem:taskreminderdb`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Task Management

#### Create Task
```bash
curl -X POST http://localhost:8080/tasks/add \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the project",
    "dueTimestamp": 1735689600000,
    "email": "user@example.com",
    "status": "PENDING"
  }'
```

#### List All Tasks
```bash
curl http://localhost:8080/tasks/list
```

#### List Tasks by Status
```bash
curl "http://localhost:8080/tasks/list?status=PENDING"
```

#### Update Task
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated task title",
    "description": "Updated description",
    "dueTimestamp": 1735689600000,
    "email": "user@example.com",
    "status": "PENDING"
  }'
```

#### Delete Task
```bash
curl -X DELETE http://localhost:8080/tasks/1
```

### Scheduling

#### Set Reminder
```bash
curl -X POST http://localhost:8080/schedule/set \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": 1,
    "timezone": "America/New_York"
  }'
```

#### Get Reminder Info
```bash
curl http://localhost:8080/reminders/1
```

### Completion

#### Mark Task as Completed
```bash
curl -X PUT http://localhost:8080/completion/mark \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1
  }'
```

#### Get Task Status
```bash
curl http://localhost:8080/status/1
```

### Reports

#### Get Overview Statistics
```bash
curl http://localhost:8080/reports/overview
```

#### Export Tasks to CSV
```bash
curl -X POST http://localhost:8080/reports/export \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PENDING"
  }' \
  --output tasks_export.csv
```

Export all tasks (no status filter):
```bash
curl -X POST http://localhost:8080/reports/export \
  -H "Content-Type: application/json" \
  -d '{}' \
  --output tasks_export.csv
```

## Task Model

The Task model has the following fields:

- `id` (Long): Auto-increment primary key
- `title` (String): Task title (required)
- `description` (String): Task description
- `dueTimestamp` (Long): Due date as epoch milliseconds
- `email` (String): Email address for notifications
- `status` (String): Task status - "PENDING" or "COMPLETED"
- `createdAt` (Long): Creation timestamp as epoch milliseconds
- `completedAt` (Long): Completion timestamp as epoch milliseconds (null if not completed)

## Email Configuration

By default, the application uses a mock email sender that logs emails to the console. To enable real email sending:

1. Edit `src/main/resources/application.properties`
2. Uncomment and configure the mail properties:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

## Example Workflow

1. **Create a task:**
   ```bash
   curl -X POST http://localhost:8080/tasks/add \
     -H "Content-Type: application/json" \
     -d '{
       "title": "Review code changes",
       "description": "Review pull request #123",
       "dueTimestamp": 1735689600000,
       "email": "developer@example.com"
     }'
   ```

2. **Schedule a reminder:**
   ```bash
   curl -X POST http://localhost:8080/schedule/set \
     -H "Content-Type: application/json" \
     -d '{"taskId": 1}'
   ```

3. **Check task status:**
   ```bash
   curl http://localhost:8080/status/1
   ```

4. **Mark task as completed:**
   ```bash
   curl -X PUT http://localhost:8080/completion/mark \
     -H "Content-Type: application/json" \
     -d '{"id": 1}'
   ```

5. **Generate overview report:**
   ```bash
   curl http://localhost:8080/reports/overview
   ```

## Notes

- Timestamps are in epoch milliseconds (Unix timestamp * 1000)
- The H2 database is in-memory and will be reset on application restart
- CSV exports are saved in the `exports/` directory
- Scheduled reminders are automatically cancelled when a task is marked as completed
- On application startup, pending tasks with future due dates are automatically scheduled

## License

This project is provided as-is for educational purposes.


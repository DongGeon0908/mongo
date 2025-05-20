# Spring Boot MongoDB Todo Application

This is a simple Todo application built with Spring Boot and MongoDB. It provides a RESTful API for managing todo items.

## Features

- Create, read, update, and delete todo items
- Store todo items in MongoDB
- RESTful API endpoints
- Exception handling with custom error responses

## Prerequisites

- JDK 11 or later
- Gradle
- MongoDB (running on localhost:27017 or configured in application.yml)

## Getting Started

### Clone the repository

```bash
git clone <repository-url>
cd mongo
```

### Configure MongoDB

The application is configured to connect to MongoDB running on localhost:27017 with a database named "todo-db". You can modify these settings in the `application.yml` file.

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: todo-db
      auto-index-creation: true
```

### Build and run the application

```bash
./gradlew bootRun
```

The application will start on port 8080 by default.

## API Endpoints

### Todo Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET    | /api/todos | Get all todos |
| GET    | /api/todos/{id} | Get a todo by ID |
| POST   | /api/todos | Create a new todo |
| PUT    | /api/todos/{id} | Update a todo |
| DELETE | /api/todos/{id} | Delete a todo |

### Request/Response Examples

#### Create a Todo

Request:
```http
POST /api/todos
Content-Type: application/json

{
  "title": "Complete project",
  "description": "Finish the Spring Boot MongoDB project",
  "completed": false
}
```

Response:
```json
{
  "id": "60f1a5b3e8c7a12345678901",
  "title": "Complete project",
  "description": "Finish the Spring Boot MongoDB project",
  "completed": false,
  "createdAt": "2023-07-16T10:30:00",
  "updatedAt": "2023-07-16T10:30:00"
}
```

## Error Handling

The application includes a global exception handler that provides consistent error responses:

- 404 Not Found: When a requested resource doesn't exist
- 400 Bad Request: For validation errors
- 500 Internal Server Error: For unexpected errors

## Project Structure

```
src/main/kotlin/com/goofy/mongo/
├── MongoApplication.kt
├── config/
│   └── MongoConfig.kt
├── domain/
│   └── todo/
│       ├── controller/
│       │   └── TodoController.kt
│       ├── dto/
│       │   └── TodoDto.kt
│       ├── entity/
│       │   └── Todo.kt
│       ├── repository/
│       │   └── TodoRepository.kt
│       └── service/
│           └── TodoService.kt
└── global/
    └── exception/
        └── GlobalExceptionHandler.kt
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

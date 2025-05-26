package com.goofy.mongo.domain.todo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Enum representing the type of operation performed on a Todo document
 */
enum class OperationType {
    CREATE, UPDATE, DELETE
}

/**
 * Entity class for storing Todo document change events
 * This is used to track changes to Todo documents for auditing and real-time data change detection
 */
@Document(collection = "todo_change_events")
data class TodoChangeEvent(
    @Id
    val id: ObjectId? = null,
    
    // The ID of the Todo document that was changed
    val todoId: String,
    
    // The user ID associated with the Todo
    val uid: String,
    
    // The type of operation performed (CREATE, UPDATE, DELETE)
    val operationType: OperationType,
    
    // The Todo document data at the time of the change
    val title: String,
    val content: String,
    val completed: Boolean,
    
    // Timestamps
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val todoCreatedAt: LocalDateTime,
    val todoUpdatedAt: LocalDateTime
)

package com.goofy.mongo.domain.todo.dto

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import java.time.LocalDateTime

class TodoChangeEventDto {
    data class Response(
        val id: String,
        val todoId: String,
        val uid: String,
        val operationType: OperationType,
        val title: String,
        val content: String,
        val completed: Boolean,
        val createdAt: LocalDateTime,
        val todoCreatedAt: LocalDateTime,
        val todoUpdatedAt: LocalDateTime
    ) {
        companion object {
            fun from(todoChangeEvent: TodoChangeEvent): Response = Response(
                id = todoChangeEvent.id!!.toHexString(),
                todoId = todoChangeEvent.todoId,
                uid = todoChangeEvent.uid,
                operationType = todoChangeEvent.operationType,
                title = todoChangeEvent.title,
                content = todoChangeEvent.content,
                completed = todoChangeEvent.completed,
                createdAt = todoChangeEvent.createdAt,
                todoCreatedAt = todoChangeEvent.todoCreatedAt,
                todoUpdatedAt = todoChangeEvent.todoUpdatedAt
            )
        }
    }
}

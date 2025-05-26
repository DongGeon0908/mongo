package com.goofy.mongo.domain.todo.dto

import com.goofy.mongo.domain.todo.entity.Todo
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

class TodoDto {
    data class CreateRequest(
        @field:NotBlank(message = "Title is required")
        val title: String,

        @field:NotBlank(message = "Content is required")
        val content: String,

        @field:NotBlank(message = "User ID is required")
        val uid: String,

        val completed: Boolean = false
    ) {
        fun toEntity(): Todo = Todo(
            title = title,
            content = content,
            uid = uid,
            completed = completed
        )
    }

    data class UpdateRequest(
        @field:NotBlank(message = "Title is required")
        val title: String,

        @field:NotBlank(message = "Content is required")
        val content: String,

        val completed: Boolean
    )

    data class Response(
        val id: String,
        val uid: String,
        val title: String,
        val content: String,
        val completed: Boolean,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    ) {
        companion object {
            fun from(todo: Todo): Response = Response(
                id = todo.id!!.toHexString(),
                uid = todo.uid,
                title = todo.title,
                content = todo.content,
                completed = todo.completed,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt
            )
        }
    }
}

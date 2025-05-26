package com.goofy.mongo.domain.todo.listener

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import com.mongodb.client.model.changestream.OperationType as MongoOperationType
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TodoChangeStreamListener(
    private val todoChangeEventRepository: TodoChangeEventRepository,
    private val mongoTemplate: MongoTemplate
) : AbstractMongoEventListener<Todo>() {

    /**
     * 저장 후 이벤트 처리 (생성 및 업데이트)
     */
    override fun onAfterSave(event: AfterSaveEvent<Todo>) {
        val todo = event.source
        val operationType = if (todo.updatedAt == todo.createdAt) {
            OperationType.CREATE
        } else {
            OperationType.UPDATE
        }

        saveChangeEvent(todo, operationType)
    }

    /**
     * 삭제 후 이벤트 처리
     */
    override fun onAfterDelete(event: AfterDeleteEvent<Todo>) {
        val document = event.source as Document
        val todoId = document.getObjectId("_id").toHexString()
        val uid = document.getString("uid")
        val title = document.getString("title")
        val content = document.getString("content")
        val completed = document.getBoolean("completed")
        val createdAt = document.get("createdAt") as LocalDateTime
        val updatedAt = document.get("updatedAt") as LocalDateTime

        val todoChangeEvent = TodoChangeEvent(
            todoId = todoId,
            uid = uid,
            operationType = OperationType.DELETE,
            title = title,
            content = content,
            completed = completed,
            todoCreatedAt = createdAt,
            todoUpdatedAt = updatedAt
        )

        todoChangeEventRepository.save(todoChangeEvent)
    }

    /**
     * Todo의 변경 이벤트 저장
     */
    private fun saveChangeEvent(todo: Todo, operationType: OperationType) {
        val todoChangeEvent = TodoChangeEvent(
            todoId = todo.id!!.toHexString(),
            uid = todo.uid,
            operationType = operationType,
            title = todo.title,
            content = todo.content,
            completed = todo.completed,
            todoCreatedAt = todo.createdAt,
            todoUpdatedAt = todo.updatedAt
        )

        todoChangeEventRepository.save(todoChangeEvent)
    }
}

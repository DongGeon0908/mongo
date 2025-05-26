package com.goofy.mongo.domain.todo.service

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoChangeEventService(
    private val todoChangeEventRepository: TodoChangeEventRepository
) {
    /**
     * Find all change events
     */
    @Transactional(readOnly = true)
    fun findAll(): List<TodoChangeEvent> {
        return todoChangeEventRepository.findAll()
    }

    /**
     * Find change events by todoId
     */
    @Transactional(readOnly = true)
    fun findByTodoId(todoId: String): List<TodoChangeEvent> {
        return todoChangeEventRepository.findByTodoId(todoId)
    }

    /**
     * Find change events by uid
     */
    @Transactional(readOnly = true)
    fun findByUid(uid: String): List<TodoChangeEvent> {
        return todoChangeEventRepository.findByUid(uid)
    }

    /**
     * Find change events by operation type
     */
    @Transactional(readOnly = true)
    fun findByOperationType(operationType: OperationType): List<TodoChangeEvent> {
        return todoChangeEventRepository.findAll()
            .filter { it.operationType == operationType }
    }

    /**
     * Get change history for a specific todo
     * Returns a list of change events sorted by creation time
     */
    @Transactional(readOnly = true)
    fun getChangeHistory(todoId: String): List<TodoChangeEvent> {
        return todoChangeEventRepository.findByTodoId(todoId)
            .sortedBy { it.createdAt }
    }
}

package com.goofy.mongo.domain.todo.controller

import com.goofy.mongo.domain.todo.dto.TodoChangeEventDto
import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.service.TodoChangeEventService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todo-changes")
class TodoChangeEventController(
    private val todoChangeEventService: TodoChangeEventService
) {
    /**
     * Get all change events
     */
    @GetMapping
    fun findAll(): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findAll()
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * Get change events by todoId
     */
    @GetMapping("/todo/{todoId}")
    fun findByTodoId(@PathVariable todoId: String): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findByTodoId(todoId)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * Get change events by uid
     */
    @GetMapping("/user/{uid}")
    fun findByUid(@PathVariable uid: String): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findByUid(uid)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * Get change events by operation type
     */
    @GetMapping("/operation/{operationType}")
    fun findByOperationType(@PathVariable operationType: OperationType): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findByOperationType(operationType)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * Get change history for a specific todo
     */
    @GetMapping("/history/{todoId}")
    fun getChangeHistory(@PathVariable todoId: String): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeHistory = todoChangeEventService.getChangeHistory(todoId)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeHistory)
    }
}

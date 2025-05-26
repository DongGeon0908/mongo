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
     * 모든 변경 이벤트 조회
     */
    @GetMapping
    fun findAll(): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findAll()
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * todoId로 변경 이벤트 조회
     */
    @GetMapping("/todo/{todoId}")
    fun findByTodoId(@PathVariable todoId: String): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findByTodoId(todoId)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * uid로 변경 이벤트 조회
     */
    @GetMapping("/user/{uid}")
    fun findByUid(@PathVariable uid: String): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findByUid(uid)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * 작업 유형별 변경 이벤트 조회
     */
    @GetMapping("/operation/{operationType}")
    fun findByOperationType(@PathVariable operationType: OperationType): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeEvents = todoChangeEventService.findByOperationType(operationType)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeEvents)
    }

    /**
     * 특정 할일의 변경 이력 조회
     */
    @GetMapping("/history/{todoId}")
    fun getChangeHistory(@PathVariable todoId: String): ResponseEntity<List<TodoChangeEventDto.Response>> {
        val changeHistory = todoChangeEventService.getChangeHistory(todoId)
            .map { TodoChangeEventDto.Response.from(it) }
        return ResponseEntity.ok(changeHistory)
    }
}

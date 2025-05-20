package com.goofy.mongo.domain.todo.controller

import com.goofy.mongo.domain.todo.dto.TodoDto
import com.goofy.mongo.domain.todo.service.TodoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
class TodoController(
    private val todoService: TodoService
) {
    @GetMapping
    fun findAll(): ResponseEntity<List<TodoDto.Response>> {
        val todos = todoService.findAll()
        return ResponseEntity.ok(todos)
    }
    
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<TodoDto.Response> {
        val todo = todoService.findById(id)
        return ResponseEntity.ok(todo)
    }
    
    @PostMapping
    fun create(@Valid @RequestBody request: TodoDto.CreateRequest): ResponseEntity<TodoDto.Response> {
        val createdTodo = todoService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo)
    }
    
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody request: TodoDto.UpdateRequest
    ): ResponseEntity<TodoDto.Response> {
        val updatedTodo = todoService.update(id, request)
        return ResponseEntity.ok(updatedTodo)
    }
    
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Unit> {
        todoService.delete(id)
        return ResponseEntity.noContent().build()
    }
}

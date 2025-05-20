package com.goofy.mongo.domain.todo.service

import com.goofy.mongo.domain.todo.dto.TodoDto
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoService(
    private val todoRepository: TodoRepository
) {
    @Transactional(readOnly = true)
    fun findAll(): List<TodoDto.Response> {
        return todoRepository.findAll()
            .map { TodoDto.Response.from(it) }
    }
    
    @Transactional(readOnly = true)
    fun findById(id: String): TodoDto.Response {
        val todo = findTodoById(id)
        return TodoDto.Response.from(todo)
    }
    
    @Transactional
    fun create(request: TodoDto.CreateRequest): TodoDto.Response {
        val todo = request.toEntity()
        val savedTodo = todoRepository.save(todo)
        return TodoDto.Response.from(savedTodo)
    }
    
    @Transactional
    fun update(id: String, request: TodoDto.UpdateRequest): TodoDto.Response {
        val todo = findTodoById(id)
        todo.update(
            title = request.title,
            content = request.content,
            completed = request.completed
        )
        
        val updatedTodo = todoRepository.save(todo)
        return TodoDto.Response.from(updatedTodo)
    }
    
    @Transactional
    fun delete(id: String) {
        val todo = findTodoById(id)
        todoRepository.delete(todo)
    }
    
    private fun findTodoById(id: String): Todo {
        return todoRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Todo not found with id: $id")
    }
}

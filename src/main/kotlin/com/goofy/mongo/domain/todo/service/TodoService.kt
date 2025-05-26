package com.goofy.mongo.domain.todo.service

import com.goofy.mongo.domain.todo.dto.TodoDto
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoRepository
import org.bson.types.ObjectId
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
        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            throw NoSuchElementException("유효하지 않은 ObjectId 형식: $id")
        }
        val todo = findTodoById(objectId)
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
        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            throw NoSuchElementException("유효하지 않은 ObjectId 형식: $id")
        }
        val todo = findTodoById(objectId)
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
        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            throw NoSuchElementException("유효하지 않은 ObjectId 형식: $id")
        }
        val todo = findTodoById(objectId)
        todoRepository.delete(todo)
    }

    private fun findTodoById(id: ObjectId): Todo {
        return todoRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("해당 id로 Todo를 찾을 수 없음: $id")
    }
}

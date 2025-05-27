package com.goofy.mongo.domain.todo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import com.goofy.mongo.domain.todo.repository.TodoRepository
import io.kotest.core.spec.style.ShouldSpec
import org.bson.types.ObjectId
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

/**
 * TodoChangeEventController에 대한 통합 테스트 (Kotest 사용)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoChangeEventControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val todoRepository: TodoRepository,
    private val todoChangeEventRepository: TodoChangeEventRepository
) : ShouldSpec({
    
    val baseUrl = "/api/todo-changes"
    
    beforeTest {
        todoRepository.deleteAll()
        todoChangeEventRepository.deleteAll()
    }
    
    afterTest {
        todoRepository.deleteAll()
        todoChangeEventRepository.deleteAll()
    }
    
    context("변경 이벤트 조회") {
        should("모든 변경 이벤트를 조회할 수 있어야 함") {
            // Given
            val todo = todoRepository.save(Todo(title = "테스트 Todo", content = "테스트 내용", uid = "user123"))
            val todoId = todo.id!!.toHexString()
            
            val event1 = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.CREATE,
                title = todo.title,
                content = todo.content,
                completed = todo.completed,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = todo.updatedAt
            )
            
            val event2 = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.UPDATE,
                title = "업데이트된 제목",
                content = "업데이트된 내용",
                completed = true,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = LocalDateTime.now()
            )
            
            todoChangeEventRepository.saveAll(listOf(event1, event2))
            
            // When & Then
            mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].todoId").value(todoId))
                .andExpect(jsonPath("$[1].todoId").value(todoId))
        }
        
        should("todoId로 변경 이벤트를 조회할 수 있어야 함") {
            // Given
            val todo = todoRepository.save(Todo(title = "테스트 Todo", content = "테스트 내용", uid = "user123"))
            val todoId = todo.id!!.toHexString()
            
            val event = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.CREATE,
                title = todo.title,
                content = todo.content,
                completed = todo.completed,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = todo.updatedAt
            )
            
            todoChangeEventRepository.save(event)
            
            // When & Then
            mockMvc.perform(get("$baseUrl/todo/$todoId"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].todoId").value(todoId))
                .andExpect(jsonPath("$[0].operationType").value(OperationType.CREATE.toString()))
        }
        
        should("uid로 변경 이벤트를 조회할 수 있어야 함") {
            // Given
            val uid = "user123"
            val todo1 = todoRepository.save(Todo(title = "Todo 1", content = "내용 1", uid = uid))
            val todo2 = todoRepository.save(Todo(title = "Todo 2", content = "내용 2", uid = uid))
            
            val event1 = createTodoChangeEvent(
                todoId = todo1.id!!.toHexString(),
                uid = uid,
                operationType = OperationType.CREATE,
                title = todo1.title,
                content = todo1.content,
                completed = todo1.completed,
                todoCreatedAt = todo1.createdAt,
                todoUpdatedAt = todo1.updatedAt
            )
            
            val event2 = createTodoChangeEvent(
                todoId = todo2.id!!.toHexString(),
                uid = uid,
                operationType = OperationType.CREATE,
                title = todo2.title,
                content = todo2.content,
                completed = todo2.completed,
                todoCreatedAt = todo2.createdAt,
                todoUpdatedAt = todo2.updatedAt
            )
            
            todoChangeEventRepository.saveAll(listOf(event1, event2))
            
            // When & Then
            mockMvc.perform(get("$baseUrl/user/$uid"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].uid").value(uid))
                .andExpect(jsonPath("$[1].uid").value(uid))
        }
        
        should("작업 유형별로 변경 이벤트를 조회할 수 있어야 함") {
            // Given
            val todo = todoRepository.save(Todo(title = "테스트 Todo", content = "테스트 내용", uid = "user123"))
            val todoId = todo.id!!.toHexString()
            
            val createEvent = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.CREATE,
                title = todo.title,
                content = todo.content,
                completed = todo.completed,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = todo.updatedAt
            )
            
            val updateEvent = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.UPDATE,
                title = "업데이트된 제목",
                content = "업데이트된 내용",
                completed = true,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = LocalDateTime.now()
            )
            
            todoChangeEventRepository.saveAll(listOf(createEvent, updateEvent))
            
            // When & Then
            mockMvc.perform(get("$baseUrl/operation/${OperationType.CREATE}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].operationType").value(OperationType.CREATE.toString()))
        }
        
        should("특정 Todo의 변경 이력을 조회할 수 있어야 함") {
            // Given
            val todo = todoRepository.save(Todo(title = "테스트 Todo", content = "테스트 내용", uid = "user123"))
            val todoId = todo.id!!.toHexString()
            
            val createEvent = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.CREATE,
                title = todo.title,
                content = todo.content,
                completed = todo.completed,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = todo.updatedAt
            )
            
            val updateEvent = createTodoChangeEvent(
                todoId = todoId,
                uid = todo.uid,
                operationType = OperationType.UPDATE,
                title = "업데이트된 제목",
                content = "업데이트된 내용",
                completed = true,
                todoCreatedAt = todo.createdAt,
                todoUpdatedAt = LocalDateTime.now()
            )
            
            todoChangeEventRepository.saveAll(listOf(createEvent, updateEvent))
            
            // When & Then
            mockMvc.perform(get("$baseUrl/history/$todoId"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isArray)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].todoId").value(todoId))
                .andExpect(jsonPath("$[1].todoId").value(todoId))
        }
    }
})

/**
 * TodoChangeEvent 객체를 생성하는 헬퍼 함수
 */
private fun createTodoChangeEvent(
    todoId: String,
    uid: String,
    operationType: OperationType,
    title: String,
    content: String,
    completed: Boolean,
    todoCreatedAt: LocalDateTime,
    todoUpdatedAt: LocalDateTime
): TodoChangeEvent {
    return TodoChangeEvent(
        todoId = todoId,
        uid = uid,
        operationType = operationType,
        title = title,
        content = content,
        completed = completed,
        todoCreatedAt = todoCreatedAt,
        todoUpdatedAt = todoUpdatedAt
    )
}

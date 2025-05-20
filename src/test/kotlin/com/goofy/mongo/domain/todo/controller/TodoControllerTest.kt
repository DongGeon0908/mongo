package com.goofy.mongo.domain.todo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goofy.mongo.domain.todo.dto.TodoDto
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var todoRepository: TodoRepository

    private val baseUrl = "/api/todos"

    @BeforeEach
    fun setup() {
        todoRepository.deleteAll()
    }

    @AfterEach
    fun cleanup() {
        todoRepository.deleteAll()
    }

    @Test
    fun `should create a new todo`() {
        // Given
        val createRequest = TodoDto.CreateRequest(
            title = "Test Todo",
            content = "Test Content",
            completed = false
        )

        // When & Then
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value(createRequest.title))
            .andExpect(jsonPath("$.content").value(createRequest.content))
            .andExpect(jsonPath("$.completed").value(createRequest.completed))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `should get all todos`() {
        // Given
        val todo1 = todoRepository.save(Todo(title = "Todo 1", content = "Content 1"))
        val todo2 = todoRepository.save(Todo(title = "Todo 2", content = "Content 2"))

        // When & Then
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value(todo1.title))
            .andExpect(jsonPath("$[1].title").value(todo2.title))
    }

    @Test
    fun `should get a todo by id`() {
        // Given
        val todo = todoRepository.save(Todo(title = "Test Todo", content = "Test Content"))

        // When & Then
        mockMvc.perform(get("$baseUrl/${todo.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(todo.id))
            .andExpect(jsonPath("$.title").value(todo.title))
            .andExpect(jsonPath("$.content").value(todo.content))
    }

    @Test
    fun `should update a todo`() {
        // Given
        val todo = todoRepository.save(Todo(title = "Original Title", content = "Original Content"))
        val updateRequest = TodoDto.UpdateRequest(
            title = "Updated Title",
            content = "Updated Content",
            completed = true
        )

        // When & Then
        mockMvc.perform(
            put("$baseUrl/${todo.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(todo.id))
            .andExpect(jsonPath("$.title").value(updateRequest.title))
            .andExpect(jsonPath("$.content").value(updateRequest.content))
            .andExpect(jsonPath("$.completed").value(updateRequest.completed))
    }

    @Test
    fun `should delete a todo`() {
        // Given
        val todo = todoRepository.save(Todo(title = "Test Todo", content = "Test Content"))

        // When & Then
        mockMvc.perform(delete("$baseUrl/${todo.id}"))
            .andExpect(status().isNoContent)

        // Verify it's deleted
        mockMvc.perform(get("$baseUrl/${todo.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 404 when todo not found`() {
        // Given
        val nonExistentId = ObjectId().toHexString() // Generate a valid but non-existent ObjectId

        // When & Then
        mockMvc.perform(get("$baseUrl/$nonExistentId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 400 when validation fails`() {
        // Given
        val invalidRequest = TodoDto.CreateRequest(
            title = "",  // Empty title should fail validation
            content = "Test Content",
            completed = false
        )

        // When & Then
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }
}

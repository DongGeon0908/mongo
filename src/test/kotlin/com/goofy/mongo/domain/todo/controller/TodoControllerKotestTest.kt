package com.goofy.mongo.domain.todo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.goofy.mongo.domain.todo.dto.TodoDto
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * TodoController에 대한 통합 테스트 (Kotest 사용)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoControllerKotestTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val todoRepository: TodoRepository
) : DescribeSpec({
    
    val baseUrl = "/api/todos"
    
    beforeTest {
        todoRepository.deleteAll()
    }
    
    afterTest {
        todoRepository.deleteAll()
    }
    
    describe("Todo API") {
        context("Todo 생성 시") {
            it("유효한 요청으로 새 Todo를 생성할 수 있어야 함") {
                // Given
                val createRequest = TodoDto.CreateRequest(
                    title = "테스트 Todo",
                    content = "테스트 내용",
                    uid = "user123",
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
                    .andExpect(jsonPath("$.uid").value(createRequest.uid))
                    .andExpect(jsonPath("$.id").exists())
            }
            
            it("유효하지 않은 요청으로는 400 Bad Request를 반환해야 함") {
                // Given
                val invalidRequest = TodoDto.CreateRequest(
                    title = "",  // 빈 제목은 유효성 검사에 실패해야 함
                    content = "테스트 내용",
                    uid = "user123",
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
        
        context("Todo 조회 시") {
            it("모든 Todo를 조회할 수 있어야 함") {
                // Given
                val todo1 = todoRepository.save(Todo(title = "Todo 1", content = "내용 1", uid = "user123"))
                val todo2 = todoRepository.save(Todo(title = "Todo 2", content = "내용 2", uid = "user123"))
                
                // When & Then
                mockMvc.perform(get(baseUrl))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value(todo1.title))
                    .andExpect(jsonPath("$[1].title").value(todo2.title))
            }
            
            it("ID로 특정 Todo를 조회할 수 있어야 함") {
                // Given
                val todo = todoRepository.save(Todo(title = "테스트 Todo", content = "테스트 내용", uid = "user123"))
                
                // When & Then
                mockMvc.perform(get("$baseUrl/${todo.id}"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(todo.id.toString()))
                    .andExpect(jsonPath("$.title").value(todo.title))
                    .andExpect(jsonPath("$.content").value(todo.content))
                    .andExpect(jsonPath("$.uid").value(todo.uid))
            }
            
            it("존재하지 않는 ID로 조회 시 404 Not Found를 반환해야 함") {
                // Given
                val nonExistentId = ObjectId().toHexString() // 존재하지 않는 유효한 ObjectId
                
                // When & Then
                mockMvc.perform(get("$baseUrl/$nonExistentId"))
                    .andExpect(status().isNotFound)
            }
        }
        
        context("Todo 업데이트 시") {
            it("유효한 요청으로 Todo를 업데이트할 수 있어야 함") {
                // Given
                val todo = todoRepository.save(Todo(title = "원래 제목", content = "원래 내용", uid = "user123"))
                val updateRequest = TodoDto.UpdateRequest(
                    title = "변경된 제목",
                    content = "변경된 내용",
                    completed = true
                )
                
                // When & Then
                mockMvc.perform(
                    put("$baseUrl/${todo.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(todo.id.toString()))
                    .andExpect(jsonPath("$.title").value(updateRequest.title))
                    .andExpect(jsonPath("$.content").value(updateRequest.content))
                    .andExpect(jsonPath("$.completed").value(updateRequest.completed))
                    .andExpect(jsonPath("$.uid").value(todo.uid))
            }
            
            it("존재하지 않는 ID로 업데이트 시 404 Not Found를 반환해야 함") {
                // Given
                val nonExistentId = ObjectId().toHexString() // 존재하지 않는 유효한 ObjectId
                val updateRequest = TodoDto.UpdateRequest(
                    title = "변경된 제목",
                    content = "변경된 내용",
                    completed = true
                )
                
                // When & Then
                mockMvc.perform(
                    put("$baseUrl/$nonExistentId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                )
                    .andExpect(status().isNotFound)
            }
        }
        
        context("Todo 삭제 시") {
            it("ID로 Todo를 삭제할 수 있어야 함") {
                // Given
                val todo = todoRepository.save(Todo(title = "삭제할 Todo", content = "삭제할 내용", uid = "user123"))
                
                // When & Then
                mockMvc.perform(delete("$baseUrl/${todo.id}"))
                    .andExpect(status().isNoContent)
                
                // 삭제 확인
                mockMvc.perform(get("$baseUrl/${todo.id}"))
                    .andExpect(status().isNotFound)
            }
            
            it("존재하지 않는 ID로 삭제 시 404 Not Found를 반환해야 함") {
                // Given
                val nonExistentId = ObjectId().toHexString() // 존재하지 않는 유효한 ObjectId
                
                // When & Then
                mockMvc.perform(delete("$baseUrl/$nonExistentId"))
                    .andExpect(status().isNotFound)
            }
        }
    }
})

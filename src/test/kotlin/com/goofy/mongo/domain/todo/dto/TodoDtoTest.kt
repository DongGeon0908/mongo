package com.goofy.mongo.domain.todo.dto

import com.goofy.mongo.domain.todo.entity.Todo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import java.time.LocalDateTime

/**
 * TodoDto 클래스에 대한 단위 테스트
 */
class TodoDtoTest : StringSpec({
    "CreateRequest가 Todo 엔티티로 올바르게 변환되어야 함" {
        // Given
        val createRequest = TodoDto.CreateRequest(
            title = "테스트 제목",
            content = "테스트 내용",
            uid = "user123",
            completed = true
        )
        
        // When
        val todo = createRequest.toEntity()
        
        // Then
        todo.title shouldBe createRequest.title
        todo.content shouldBe createRequest.content
        todo.uid shouldBe createRequest.uid
        todo.completed shouldBe createRequest.completed
    }
    
    "Todo 엔티티가 Response DTO로 올바르게 변환되어야 함" {
        // Given
        val objectId = ObjectId()
        val now = LocalDateTime.now()
        val todo = Todo(
            uid = "user123",
            title = "테스트 제목",
            content = "테스트 내용",
            completed = true
        )
        
        // ObjectId와 날짜 필드를 리플렉션을 통해 설정
        val idField = Todo::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(todo, objectId)
        
        val createdAtField = Todo::class.java.getDeclaredField("createdAt")
        createdAtField.isAccessible = true
        createdAtField.set(todo, now)
        
        val updatedAtField = Todo::class.java.getDeclaredField("updatedAt")
        updatedAtField.isAccessible = true
        updatedAtField.set(todo, now)
        
        // When
        val response = TodoDto.Response.from(todo)
        
        // Then
        response.id shouldBe objectId.toHexString()
        response.uid shouldBe todo.uid
        response.title shouldBe todo.title
        response.content shouldBe todo.content
        response.completed shouldBe todo.completed
        response.createdAt shouldBe now
        response.updatedAt shouldBe now
    }
})

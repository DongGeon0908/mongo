package com.goofy.mongo.domain.todo.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Todo 엔티티에 대한 단위 테스트
 */
class TodoTest : StringSpec({
    "Todo 생성 시 기본값이 올바르게 설정되어야 함" {
        // Given
        val uid = "user123"
        val title = "테스트 제목"
        val content = "테스트 내용"
        
        // When
        val todo = Todo(
            uid = uid,
            title = title,
            content = content
        )
        
        // Then
        todo.uid shouldBe uid
        todo.title shouldBe title
        todo.content shouldBe content
        todo.completed shouldBe false
    }
    
    "Todo 업데이트 시 필드가 올바르게 변경되어야 함" {
        // Given
        val todo = Todo(
            uid = "user123",
            title = "원래 제목",
            content = "원래 내용"
        )
        val newTitle = "변경된 제목"
        val newContent = "변경된 내용"
        val newCompleted = true
        
        // When
        todo.update(
            title = newTitle,
            content = newContent,
            completed = newCompleted
        )
        
        // Then
        todo.title shouldBe newTitle
        todo.content shouldBe newContent
        todo.completed shouldBe newCompleted
    }
})

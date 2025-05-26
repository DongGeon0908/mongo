package com.goofy.mongo.domain.todo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Todo 문서에 수행된 작업 유형을 나타내는 열거형
 */
enum class OperationType {
    CREATE, UPDATE, DELETE
}

/**
 * Todo 문서 변경 이벤트를 저장하는 엔티티 클래스
 * 감사 및 실시간 데이터 변경 감지를 위해 Todo 문서의 변경 사항을 추적하는 데 사용됨
 */
@Document(collection = "todo_change_events")
data class TodoChangeEvent(
    @Id
    val id: ObjectId? = null,

    // 변경된 Todo 문서의 ID
    val todoId: String,

    // Todo와 연결된 사용자 ID
    val uid: String,

    // 수행된 작업 유형 (생성, 수정, 삭제)
    val operationType: OperationType,

    // 변경 시점의 Todo 문서 데이터
    val title: String,
    val content: String,
    val completed: Boolean,

    // 타임스탬프
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val todoCreatedAt: LocalDateTime,
    val todoUpdatedAt: LocalDateTime
)

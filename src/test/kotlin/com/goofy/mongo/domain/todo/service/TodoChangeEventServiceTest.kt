package com.goofy.mongo.domain.todo.service

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

/**
 * TodoChangeEventService에 대한 단위 테스트
 */
class TodoChangeEventServiceTest : FunSpec({
    // 목 객체 생성
    val todoChangeEventRepository = mock<TodoChangeEventRepository>()
    val todoChangeEventService = TodoChangeEventService(todoChangeEventRepository)
    
    // 테스트 데이터 설정
    val now = LocalDateTime.now()
    val todoId = ObjectId().toHexString()
    val uid = "user123"
    
    test("findAll은 모든 변경 이벤트를 반환해야 함") {
        // Given
        val events = listOf(
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = todoId,
                uid = uid,
                operationType = OperationType.CREATE,
                createdAt = now.minusHours(2)
            ),
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = todoId,
                uid = uid,
                operationType = OperationType.UPDATE,
                createdAt = now.minusHours(1)
            )
        )
        
        whenever(todoChangeEventRepository.findAll()).thenReturn(events)
        
        // When
        val result = todoChangeEventService.findAll()
        
        // Then
        result shouldHaveSize 2
        result[0].operationType shouldBe OperationType.CREATE
        result[1].operationType shouldBe OperationType.UPDATE
    }
    
    test("findByTodoId는 특정 todoId의 변경 이벤트를 반환해야 함") {
        // Given
        val events = listOf(
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = todoId,
                uid = uid,
                operationType = OperationType.CREATE,
                createdAt = now.minusHours(2)
            ),
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = todoId,
                uid = uid,
                operationType = OperationType.UPDATE,
                createdAt = now.minusHours(1)
            )
        )
        
        whenever(todoChangeEventRepository.findByTodoId(todoId)).thenReturn(events)
        
        // When
        val result = todoChangeEventService.findByTodoId(todoId)
        
        // Then
        result shouldHaveSize 2
        result[0].todoId shouldBe todoId
        result[1].todoId shouldBe todoId
    }
    
    test("findByUid는 특정 uid의 변경 이벤트를 반환해야 함") {
        // Given
        val events = listOf(
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = todoId,
                uid = uid,
                operationType = OperationType.CREATE,
                createdAt = now.minusHours(2)
            ),
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = ObjectId().toHexString(),
                uid = uid,
                operationType = OperationType.CREATE,
                createdAt = now.minusHours(1)
            )
        )
        
        whenever(todoChangeEventRepository.findByUid(uid)).thenReturn(events)
        
        // When
        val result = todoChangeEventService.findByUid(uid)
        
        // Then
        result shouldHaveSize 2
        result[0].uid shouldBe uid
        result[1].uid shouldBe uid
    }
    
    test("findByOperationType은 특정 작업 유형의 변경 이벤트를 반환해야 함") {
        // Given
        val events = listOf(
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = todoId,
                uid = uid,
                operationType = OperationType.CREATE,
                createdAt = now.minusHours(2)
            ),
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = ObjectId().toHexString(),
                uid = uid,
                operationType = OperationType.UPDATE,
                createdAt = now.minusHours(1)
            ),
            createTodoChangeEvent(
                id = ObjectId(),
                todoId = ObjectId().toHexString(),
                uid = uid,
                operationType = OperationType.DELETE,
                createdAt = now
            )
        )
        
        whenever(todoChangeEventRepository.findAll()).thenReturn(events)
        
        // When
        val result = todoChangeEventService.findByOperationType(OperationType.CREATE)
        
        // Then
        result shouldHaveSize 1
        result[0].operationType shouldBe OperationType.CREATE
    }
    
    test("getChangeHistory는 생성 시간순으로 정렬된 변경 이력을 반환해야 함") {
        // Given
        val event1 = createTodoChangeEvent(
            id = ObjectId(),
            todoId = todoId,
            uid = uid,
            operationType = OperationType.CREATE,
            createdAt = now.minusHours(2)
        )
        val event2 = createTodoChangeEvent(
            id = ObjectId(),
            todoId = todoId,
            uid = uid,
            operationType = OperationType.UPDATE,
            createdAt = now.minusHours(1)
        )
        val event3 = createTodoChangeEvent(
            id = ObjectId(),
            todoId = todoId,
            uid = uid,
            operationType = OperationType.DELETE,
            createdAt = now
        )
        
        // 의도적으로 순서를 섞어서 반환
        whenever(todoChangeEventRepository.findByTodoId(todoId)).thenReturn(listOf(event2, event3, event1))
        
        // When
        val result = todoChangeEventService.getChangeHistory(todoId)
        
        // Then
        result shouldHaveSize 3
        result[0].createdAt shouldBe event1.createdAt
        result[1].createdAt shouldBe event2.createdAt
        result[2].createdAt shouldBe event3.createdAt
    }
    
    test("존재하지 않는 todoId로 조회하면 빈 목록을 반환해야 함") {
        // Given
        whenever(todoChangeEventRepository.findByTodoId("nonexistent")).thenReturn(emptyList())
        
        // When
        val result = todoChangeEventService.findByTodoId("nonexistent")
        
        // Then
        result.shouldBeEmpty()
    }
})

/**
 * TodoChangeEvent 객체를 생성하는 헬퍼 함수
 */
private fun createTodoChangeEvent(
    id: ObjectId,
    todoId: String,
    uid: String,
    operationType: OperationType,
    title: String = "테스트 제목",
    content: String = "테스트 내용",
    completed: Boolean = false,
    createdAt: LocalDateTime,
    todoCreatedAt: LocalDateTime = LocalDateTime.now().minusDays(1),
    todoUpdatedAt: LocalDateTime = LocalDateTime.now()
): TodoChangeEvent {
    val event = TodoChangeEvent(
        todoId = todoId,
        uid = uid,
        operationType = operationType,
        title = title,
        content = content,
        completed = completed,
        todoCreatedAt = todoCreatedAt,
        todoUpdatedAt = todoUpdatedAt
    )
    
    // 리플렉션을 사용하여 읽기 전용 필드 설정
    val idField = TodoChangeEvent::class.java.getDeclaredField("id")
    idField.isAccessible = true
    idField.set(event, id)
    
    val eventCreatedAtField = TodoChangeEvent::class.java.getDeclaredField("createdAt")
    eventCreatedAtField.isAccessible = true
    eventCreatedAtField.set(event, createdAt)
    
    return event
}

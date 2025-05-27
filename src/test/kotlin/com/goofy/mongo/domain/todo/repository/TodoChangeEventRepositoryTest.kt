package com.goofy.mongo.domain.todo.repository

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

/**
 * TodoChangeEventRepository에 대한 통합 테스트 (Kotest 사용)
 */
@SpringBootTest
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoChangeEventRepositoryTest(
    private val todoChangeEventRepository: TodoChangeEventRepository
) : FreeSpec({
    
    beforeTest {
        todoChangeEventRepository.deleteAll()
    }
    
    afterTest {
        todoChangeEventRepository.deleteAll()
    }
    
    "TodoChangeEventRepository" - {
        "변경 이벤트 저장" - {
            "TodoChangeEvent 엔티티를 저장할 수 있어야 함" {
                // Given
                val todoId = ObjectId().toHexString()
                val event = TodoChangeEvent(
                    todoId = todoId,
                    uid = "user123",
                    operationType = OperationType.CREATE,
                    title = "테스트 제목",
                    content = "테스트 내용",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                
                // When
                val savedEvent = todoChangeEventRepository.save(event)
                
                // Then
                savedEvent.id shouldNotBe null
                savedEvent.todoId shouldBe todoId
                savedEvent.uid shouldBe "user123"
                savedEvent.operationType shouldBe OperationType.CREATE
                savedEvent.title shouldBe "테스트 제목"
                savedEvent.content shouldBe "테스트 내용"
                savedEvent.completed shouldBe false
                savedEvent.createdAt shouldNotBe null
            }
        }
        
        "변경 이벤트 조회" - {
            "ID로 TodoChangeEvent 엔티티를 조회할 수 있어야 함" {
                // Given
                val todoId = ObjectId().toHexString()
                val event = TodoChangeEvent(
                    todoId = todoId,
                    uid = "user123",
                    operationType = OperationType.CREATE,
                    title = "테스트 제목",
                    content = "테스트 내용",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                val savedEvent = todoChangeEventRepository.save(event)
                
                // When
                val foundEvent = todoChangeEventRepository.findById(savedEvent.id!!)
                
                // Then
                foundEvent.isPresent shouldBe true
                foundEvent.get().id shouldBe savedEvent.id
                foundEvent.get().todoId shouldBe todoId
                foundEvent.get().operationType shouldBe OperationType.CREATE
            }
            
            "todoId로 TodoChangeEvent 엔티티를 조회할 수 있어야 함" {
                // Given
                val todoId = ObjectId().toHexString()
                val event1 = TodoChangeEvent(
                    todoId = todoId,
                    uid = "user123",
                    operationType = OperationType.CREATE,
                    title = "원래 제목",
                    content = "원래 내용",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                val event2 = TodoChangeEvent(
                    todoId = todoId,
                    uid = "user123",
                    operationType = OperationType.UPDATE,
                    title = "변경된 제목",
                    content = "변경된 내용",
                    completed = true,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                
                todoChangeEventRepository.saveAll(listOf(event1, event2))
                
                // When
                val events = todoChangeEventRepository.findByTodoId(todoId)
                
                // Then
                events shouldHaveSize 2
                events[0].todoId shouldBe todoId
                events[1].todoId shouldBe todoId
                events.map { it.operationType } shouldBe listOf(OperationType.CREATE, OperationType.UPDATE)
            }
            
            "uid로 TodoChangeEvent 엔티티를 조회할 수 있어야 함" {
                // Given
                val uid = "user123"
                val todoId1 = ObjectId().toHexString()
                val todoId2 = ObjectId().toHexString()
                
                val event1 = TodoChangeEvent(
                    todoId = todoId1,
                    uid = uid,
                    operationType = OperationType.CREATE,
                    title = "Todo 1",
                    content = "내용 1",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                val event2 = TodoChangeEvent(
                    todoId = todoId2,
                    uid = uid,
                    operationType = OperationType.CREATE,
                    title = "Todo 2",
                    content = "내용 2",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                
                todoChangeEventRepository.saveAll(listOf(event1, event2))
                
                // When
                val events = todoChangeEventRepository.findByUid(uid)
                
                // Then
                events shouldHaveSize 2
                events[0].uid shouldBe uid
                events[1].uid shouldBe uid
                events.map { it.todoId } shouldBe listOf(todoId1, todoId2)
            }
            
            "모든 TodoChangeEvent 엔티티를 조회할 수 있어야 함" {
                // Given
                val todoId1 = ObjectId().toHexString()
                val todoId2 = ObjectId().toHexString()
                
                val event1 = TodoChangeEvent(
                    todoId = todoId1,
                    uid = "user123",
                    operationType = OperationType.CREATE,
                    title = "Todo 1",
                    content = "내용 1",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                val event2 = TodoChangeEvent(
                    todoId = todoId2,
                    uid = "user456",
                    operationType = OperationType.CREATE,
                    title = "Todo 2",
                    content = "내용 2",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                
                todoChangeEventRepository.saveAll(listOf(event1, event2))
                
                // When
                val allEvents = todoChangeEventRepository.findAll()
                
                // Then
                allEvents shouldHaveSize 2
                allEvents.map { it.todoId } shouldBe listOf(todoId1, todoId2)
            }
        }
        
        "변경 이벤트 삭제" - {
            "TodoChangeEvent 엔티티를 삭제할 수 있어야 함" {
                // Given
                val todoId = ObjectId().toHexString()
                val event = TodoChangeEvent(
                    todoId = todoId,
                    uid = "user123",
                    operationType = OperationType.CREATE,
                    title = "삭제할 제목",
                    content = "삭제할 내용",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                val savedEvent = todoChangeEventRepository.save(event)
                
                // When
                todoChangeEventRepository.delete(savedEvent)
                
                // Then
                val foundEvent = todoChangeEventRepository.findById(savedEvent.id!!)
                foundEvent.isPresent shouldBe false
            }
            
            "모든 TodoChangeEvent 엔티티를 삭제할 수 있어야 함" {
                // Given
                val todoId1 = ObjectId().toHexString()
                val todoId2 = ObjectId().toHexString()
                
                val event1 = TodoChangeEvent(
                    todoId = todoId1,
                    uid = "user123",
                    operationType = OperationType.CREATE,
                    title = "Todo 1",
                    content = "내용 1",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                val event2 = TodoChangeEvent(
                    todoId = todoId2,
                    uid = "user456",
                    operationType = OperationType.CREATE,
                    title = "Todo 2",
                    content = "내용 2",
                    completed = false,
                    todoCreatedAt = LocalDateTime.now(),
                    todoUpdatedAt = LocalDateTime.now()
                )
                
                todoChangeEventRepository.saveAll(listOf(event1, event2))
                
                // When
                todoChangeEventRepository.deleteAll()
                
                // Then
                val allEvents = todoChangeEventRepository.findAll()
                allEvents.shouldBeEmpty()
            }
        }
    }
})

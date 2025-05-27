package com.goofy.mongo.domain.todo.repository

import com.goofy.mongo.domain.todo.entity.Todo
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

/**
 * TodoRepository에 대한 통합 테스트 (Kotest 사용)
 */
@SpringBootTest
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoRepositoryTest(
    private val todoRepository: TodoRepository
) : WordSpec({
    
    beforeTest {
        todoRepository.deleteAll()
    }
    
    afterTest {
        todoRepository.deleteAll()
    }
    
    "TodoRepository" should {
        "Todo 엔티티를 저장할 수 있어야 함" {
            // Given
            val todo = Todo(
                title = "테스트 제목",
                content = "테스트 내용",
                uid = "user123"
            )
            
            // When
            val savedTodo = todoRepository.save(todo)
            
            // Then
            savedTodo.id shouldNotBe null
            savedTodo.title shouldBe "테스트 제목"
            savedTodo.content shouldBe "테스트 내용"
            savedTodo.uid shouldBe "user123"
            savedTodo.completed shouldBe false
            savedTodo.createdAt shouldNotBe null
            savedTodo.updatedAt shouldNotBe null
        }
        
        "ID로 Todo 엔티티를 조회할 수 있어야 함" {
            // Given
            val todo = Todo(
                title = "테스트 제목",
                content = "테스트 내용",
                uid = "user123"
            )
            val savedTodo = todoRepository.save(todo)
            
            // When
            val foundTodo = todoRepository.findById(savedTodo.id!!)
            
            // Then
            foundTodo.isPresent shouldBe true
            foundTodo.get().id shouldBe savedTodo.id
            foundTodo.get().title shouldBe savedTodo.title
            foundTodo.get().content shouldBe savedTodo.content
            foundTodo.get().uid shouldBe savedTodo.uid
        }
        
        "모든 Todo 엔티티를 조회할 수 있어야 함" {
            // Given
            val todo1 = Todo(title = "제목 1", content = "내용 1", uid = "user123")
            val todo2 = Todo(title = "제목 2", content = "내용 2", uid = "user123")
            val todo3 = Todo(title = "제목 3", content = "내용 3", uid = "user456")
            
            todoRepository.saveAll(listOf(todo1, todo2, todo3))
            
            // When
            val allTodos = todoRepository.findAll()
            
            // Then
            allTodos shouldHaveSize 3
            allTodos.map { it.title } shouldContain "제목 1"
            allTodos.map { it.title } shouldContain "제목 2"
            allTodos.map { it.title } shouldContain "제목 3"
        }
        
        "Todo 엔티티를 삭제할 수 있어야 함" {
            // Given
            val todo = Todo(
                title = "삭제할 제목",
                content = "삭제할 내용",
                uid = "user123"
            )
            val savedTodo = todoRepository.save(todo)
            
            // When
            todoRepository.delete(savedTodo)
            
            // Then
            val foundTodo = todoRepository.findById(savedTodo.id!!)
            foundTodo.isPresent shouldBe false
        }
        
        "모든 Todo 엔티티를 삭제할 수 있어야 함" {
            // Given
            val todo1 = Todo(title = "제목 1", content = "내용 1", uid = "user123")
            val todo2 = Todo(title = "제목 2", content = "내용 2", uid = "user123")
            
            todoRepository.saveAll(listOf(todo1, todo2))
            
            // When
            todoRepository.deleteAll()
            
            // Then
            val allTodos = todoRepository.findAll()
            allTodos.shouldBeEmpty()
        }
    }
})

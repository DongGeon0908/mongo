package com.goofy.mongo.domain.todo.listener

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import com.goofy.mongo.domain.todo.repository.TodoRepository
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import io.kotest.core.annotation.AutoScan

/**
 * TodoChangeStreamListener에 대한 통합 테스트 (Kotest 사용)
 */
@AutoScan
@SpringBootTest
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoChangeStreamListenerKotestTest(
    private val todoRepository: TodoRepository,
    private val todoChangeEventRepository: TodoChangeEventRepository
) : ExpectSpec({

    beforeTest {
        todoRepository.deleteAll()
        todoChangeEventRepository.deleteAll()
    }

    afterTest {
        todoRepository.deleteAll()
        todoChangeEventRepository.deleteAll()
    }

    context("Todo 생성 시") {
        expect("CREATE 유형의 변경 이벤트가 생성되어야 함") {
            // Given
            val todo = Todo(
                title = "테스트 Todo",
                content = "테스트 내용",
                uid = "user123"
            )

            // When
            val savedTodo = todoRepository.save(todo)

            // Then
            val changeEvents = todoChangeEventRepository.findByTodoId(savedTodo.id!!.toHexString())
            changeEvents shouldHaveSize 1

            val changeEvent = changeEvents.first()
            changeEvent.operationType shouldBe OperationType.CREATE
            changeEvent.todoId shouldBe savedTodo.id!!.toHexString()
            changeEvent.uid shouldBe savedTodo.uid
            changeEvent.title shouldBe savedTodo.title
            changeEvent.content shouldBe savedTodo.content
            changeEvent.completed shouldBe savedTodo.completed
            changeEvent.todoCreatedAt shouldBe savedTodo.createdAt
            changeEvent.todoUpdatedAt shouldBe savedTodo.updatedAt
        }
    }

    context("Todo 업데이트 시") {
        expect("UPDATE 유형의 변경 이벤트가 생성되어야 함") {
            // Given
            val todo = todoRepository.save(
                Todo(
                    title = "원래 제목",
                    content = "원래 내용",
                    uid = "user123"
                )
            )

            // 생성 이벤트 초기화
            todoChangeEventRepository.deleteAll()

            // When
            todo.update(
                title = "변경된 제목",
                content = "변경된 내용",
                completed = true
            )
            val updatedTodo = todoRepository.save(todo)

            // Then
            val changeEvents = todoChangeEventRepository.findByTodoId(updatedTodo.id!!.toHexString())
            changeEvents shouldHaveSize 1

            val changeEvent = changeEvents.first()
            changeEvent.operationType shouldBe OperationType.UPDATE
            changeEvent.todoId shouldBe updatedTodo.id!!.toHexString()
            changeEvent.uid shouldBe updatedTodo.uid
            changeEvent.title shouldBe updatedTodo.title
            changeEvent.content shouldBe updatedTodo.content
            changeEvent.completed shouldBe updatedTodo.completed
            changeEvent.todoCreatedAt shouldBe updatedTodo.createdAt
            changeEvent.todoUpdatedAt shouldBe updatedTodo.updatedAt
        }
    }

    context("Todo 삭제 시") {
        expect("DELETE 유형의 변경 이벤트가 생성되어야 함") {
            // Given
            val todo = todoRepository.save(
                Todo(
                    title = "삭제할 Todo",
                    content = "삭제할 내용",
                    uid = "user123"
                )
            )

            // 생성 이벤트 초기화
            todoChangeEventRepository.deleteAll()

            // When
            todoRepository.delete(todo)

            // Then
            val changeEvents = todoChangeEventRepository.findByTodoId(todo.id!!.toHexString())
            changeEvents shouldHaveSize 1

            val changeEvent = changeEvents.first()
            changeEvent.operationType shouldBe OperationType.DELETE
            changeEvent.todoId shouldBe todo.id!!.toHexString()
            changeEvent.uid shouldBe todo.uid
            changeEvent.title shouldBe todo.title
            changeEvent.content shouldBe todo.content
            changeEvent.completed shouldBe todo.completed
        }
    }
})

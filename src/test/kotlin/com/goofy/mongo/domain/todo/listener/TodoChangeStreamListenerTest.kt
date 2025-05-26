package com.goofy.mongo.domain.todo.listener

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import com.goofy.mongo.domain.todo.repository.TodoRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = [
    "spring.data.mongodb.host=localhost",
    "spring.data.mongodb.port=27017",
    "spring.data.mongodb.database=todo-test-db"
])
class TodoChangeStreamListenerTest {

    @Autowired
    private lateinit var todoRepository: TodoRepository

    @Autowired
    private lateinit var todoChangeEventRepository: TodoChangeEventRepository

    @BeforeEach
    fun setup() {
        todoRepository.deleteAll()
        todoChangeEventRepository.deleteAll()
    }

    @AfterEach
    fun cleanup() {
        todoRepository.deleteAll()
        todoChangeEventRepository.deleteAll()
    }

    @Test
    fun `should create a change event when a todo is created`() {
        // 주어진 조건
        val todo = Todo(
            title = "Test Todo",
            content = "Test Content",
            uid = "user123"
        )

        // 실행
        val savedTodo = todoRepository.save(todo)

        // 검증
        val changeEvents = todoChangeEventRepository.findByTodoId(savedTodo.id!!.toHexString())
        assertEquals(1, changeEvents.size)

        val changeEvent = changeEvents.first()
        assertEquals(OperationType.CREATE, changeEvent.operationType)
        assertEquals(savedTodo.id!!.toHexString(), changeEvent.todoId)
        assertEquals(savedTodo.uid, changeEvent.uid)
        assertEquals(savedTodo.title, changeEvent.title)
        assertEquals(savedTodo.content, changeEvent.content)
        assertEquals(savedTodo.completed, changeEvent.completed)
        assertEquals(savedTodo.createdAt, changeEvent.todoCreatedAt)
        assertEquals(savedTodo.updatedAt, changeEvent.todoUpdatedAt)
    }

    @Test
    fun `should create a change event when a todo is updated`() {
        // 주어진 조건
        val todo = todoRepository.save(
            Todo(
                title = "Original Title",
                content = "Original Content",
                uid = "user123"
            )
        )

        // 생성 이벤트 초기화
        todoChangeEventRepository.deleteAll()

        // 실행
        todo.update(
            title = "Updated Title",
            content = "Updated Content",
            completed = true
        )
        val updatedTodo = todoRepository.save(todo)

        // 검증
        val changeEvents = todoChangeEventRepository.findByTodoId(updatedTodo.id!!.toHexString())
        assertEquals(1, changeEvents.size)

        val changeEvent = changeEvents.first()
        assertEquals(OperationType.UPDATE, changeEvent.operationType)
        assertEquals(updatedTodo.id!!.toHexString(), changeEvent.todoId)
        assertEquals(updatedTodo.uid, changeEvent.uid)
        assertEquals(updatedTodo.title, changeEvent.title)
        assertEquals(updatedTodo.content, changeEvent.content)
        assertEquals(updatedTodo.completed, changeEvent.completed)
        assertEquals(updatedTodo.createdAt, changeEvent.todoCreatedAt)
        assertEquals(updatedTodo.updatedAt, changeEvent.todoUpdatedAt)
    }

    @Test
    fun `should create a change event when a todo is deleted`() {
        // 주어진 조건
        val todo = todoRepository.save(
            Todo(
                title = "Test Todo",
                content = "Test Content",
                uid = "user123"
            )
        )

        // 생성 이벤트 초기화
        todoChangeEventRepository.deleteAll()

        // 실행
        todoRepository.delete(todo)

        // 검증
        val changeEvents = todoChangeEventRepository.findByTodoId(todo.id!!.toHexString())
        assertEquals(1, changeEvents.size)

        val changeEvent = changeEvents.first()
        assertEquals(OperationType.DELETE, changeEvent.operationType)
        assertEquals(todo.id!!.toHexString(), changeEvent.todoId)
        assertEquals(todo.uid, changeEvent.uid)
        assertEquals(todo.title, changeEvent.title)
        assertEquals(todo.content, changeEvent.content)
        assertEquals(todo.completed, changeEvent.completed)
    }
}

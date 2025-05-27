package com.goofy.mongo.domain.todo.service

import com.goofy.mongo.domain.todo.dto.TodoDto
import com.goofy.mongo.domain.todo.entity.Todo
import com.goofy.mongo.domain.todo.repository.TodoRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.NoSuchElementException
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.any

/**
 * TodoService에 대한 단위 테스트
 */
class TodoServiceTest : BehaviorSpec({
    // 목 객체 생성
    val todoRepository = mock<TodoRepository>()
    val todoService = TodoService(todoRepository)

    // 테스트 데이터 설정
    val objectId = ObjectId()
    val hexId = objectId.toHexString()
    val now = LocalDateTime.now()

    Given("Todo 목록이 있을 때") {
        val todo1 = createTodoWithId(
            objectId = ObjectId(),
            uid = "user1",
            title = "제목 1",
            content = "내용 1",
            createdAt = now,
            updatedAt = now
        )
        val todo2 = createTodoWithId(
            objectId = ObjectId(),
            uid = "user1",
            title = "제목 2",
            content = "내용 2",
            createdAt = now,
            updatedAt = now
        )

        whenever(todoRepository.findAll()).thenReturn(listOf(todo1, todo2))

        When("모든 Todo를 조회하면") {
            val result = todoService.findAll()

            Then("모든 Todo가 반환되어야 함") {
                result.size shouldBe 2
                result[0].title shouldBe "제목 1"
                result[1].title shouldBe "제목 2"
            }
        }
    }

    Given("특정 ID의 Todo가 있을 때") {
        val todo = createTodoWithId(
            objectId = objectId,
            uid = "user1",
            title = "테스트 제목",
            content = "테스트 내용",
            createdAt = now,
            updatedAt = now
        )

        whenever(todoRepository.findByIdOrNull(objectId)).thenReturn(todo)

        When("해당 ID로 Todo를 조회하면") {
            val result = todoService.findById(hexId)

            Then("해당 Todo가 반환되어야 함") {
                result.id shouldBe hexId
                result.title shouldBe "테스트 제목"
                result.content shouldBe "테스트 내용"
                result.uid shouldBe "user1"
            }
        }
    }

    Given("존재하지 않는 ID로 조회할 때") {
        whenever(todoRepository.findByIdOrNull(any())).thenReturn(null)

        When("해당 ID로 Todo를 조회하면") {
            Then("NoSuchElementException이 발생해야 함") {
                shouldThrow<NoSuchElementException> {
                    todoService.findById(hexId)
                }
            }
        }
    }

    Given("새로운 Todo를 생성할 때") {
        val createRequest = TodoDto.CreateRequest(
            title = "새 제목",
            content = "새 내용",
            uid = "user1"
        )

        val newTodo = createRequest.toEntity()
        val savedTodo = createTodoWithId(
            objectId = objectId,
            uid = newTodo.uid,
            title = newTodo.title,
            content = newTodo.content,
            createdAt = now,
            updatedAt = now
        )

        whenever(todoRepository.save(any())).thenReturn(savedTodo)

        When("Todo를 저장하면") {
            val result = todoService.create(createRequest)

            Then("저장된 Todo가 반환되어야 함") {
                result.id shouldBe hexId
                result.title shouldBe "새 제목"
                result.content shouldBe "새 내용"
                result.uid shouldBe "user1"
            }
        }
    }

    Given("기존 Todo를 업데이트할 때") {
        val todo = createTodoWithId(
            objectId = objectId,
            uid = "user1",
            title = "원래 제목",
            content = "원래 내용",
            createdAt = now,
            updatedAt = now
        )

        val updateRequest = TodoDto.UpdateRequest(
            title = "변경된 제목",
            content = "변경된 내용",
            completed = true
        )

        whenever(todoRepository.findByIdOrNull(objectId)).thenReturn(todo)
        whenever(todoRepository.save(any())).thenAnswer { it.arguments[0] }

        When("Todo를 업데이트하면") {
            val result = todoService.update(hexId, updateRequest)

            Then("업데이트된 Todo가 반환되어야 함") {
                result.id shouldBe hexId
                result.title shouldBe "변경된 제목"
                result.content shouldBe "변경된 내용"
                result.completed shouldBe true
            }
        }
    }

    Given("Todo를 삭제할 때") {
        val todo = createTodoWithId(
            objectId = objectId,
            uid = "user1",
            title = "삭제할 제목",
            content = "삭제할 내용",
            createdAt = now,
            updatedAt = now
        )

        whenever(todoRepository.findByIdOrNull(objectId)).thenReturn(todo)

        When("Todo를 삭제하면") {
            todoService.delete(hexId)

            Then("repository의 delete 메소드가 호출되어야 함") {
                verify(todoRepository).delete(todo)
            }
        }
    }
})

/**
 * ID와 날짜가 설정된 Todo 객체를 생성하는 헬퍼 함수
 */
private fun createTodoWithId(
    objectId: ObjectId,
    uid: String,
    title: String,
    content: String,
    completed: Boolean = false,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime
): Todo {
    val todo = Todo(
        uid = uid,
        title = title,
        content = content,
        completed = completed
    )

    // 리플렉션을 사용하여 읽기 전용 필드 설정
    val idField = Todo::class.java.getDeclaredField("id")
    idField.isAccessible = true
    idField.set(todo, objectId)

    val createdAtField = Todo::class.java.getDeclaredField("createdAt")
    createdAtField.isAccessible = true
    createdAtField.set(todo, createdAt)

    val updatedAtField = Todo::class.java.getDeclaredField("updatedAt")
    updatedAtField.isAccessible = true
    updatedAtField.set(todo, updatedAt)

    return todo
}

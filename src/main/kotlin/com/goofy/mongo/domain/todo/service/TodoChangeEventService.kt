package com.goofy.mongo.domain.todo.service

import com.goofy.mongo.domain.todo.entity.OperationType
import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import com.goofy.mongo.domain.todo.repository.TodoChangeEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoChangeEventService(
    private val todoChangeEventRepository: TodoChangeEventRepository
) {
    /**
     * 모든 변경 이벤트 조회
     */
    @Transactional(readOnly = true)
    fun findAll(): List<TodoChangeEvent> {
        return todoChangeEventRepository.findAll()
    }

    /**
     * todoId로 변경 이벤트 조회
     */
    @Transactional(readOnly = true)
    fun findByTodoId(todoId: String): List<TodoChangeEvent> {
        return todoChangeEventRepository.findByTodoId(todoId)
    }

    /**
     * uid로 변경 이벤트 조회
     */
    @Transactional(readOnly = true)
    fun findByUid(uid: String): List<TodoChangeEvent> {
        return todoChangeEventRepository.findByUid(uid)
    }

    /**
     * 작업 유형별 변경 이벤트 조회
     */
    @Transactional(readOnly = true)
    fun findByOperationType(operationType: OperationType): List<TodoChangeEvent> {
        return todoChangeEventRepository.findAll()
            .filter { it.operationType == operationType }
    }

    /**
     * 특정 할일의 변경 이력 조회
     * 생성 시간순으로 정렬된 변경 이벤트 목록 반환
     */
    @Transactional(readOnly = true)
    fun getChangeHistory(todoId: String): List<TodoChangeEvent> {
        return todoChangeEventRepository.findByTodoId(todoId)
            .sortedBy { it.createdAt }
    }
}

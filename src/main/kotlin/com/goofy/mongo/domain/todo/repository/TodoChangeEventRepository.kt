package com.goofy.mongo.domain.todo.repository

import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoChangeEventRepository : MongoRepository<TodoChangeEvent, ObjectId> {
    // todoId로 변경 이벤트 조회
    fun findByTodoId(todoId: String): List<TodoChangeEvent>

    // uid로 변경 이벤트 조회
    fun findByUid(uid: String): List<TodoChangeEvent>
}

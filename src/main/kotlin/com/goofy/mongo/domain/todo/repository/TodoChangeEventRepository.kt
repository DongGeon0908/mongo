package com.goofy.mongo.domain.todo.repository

import com.goofy.mongo.domain.todo.entity.TodoChangeEvent
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoChangeEventRepository : MongoRepository<TodoChangeEvent, ObjectId> {
    // Find change events by todoId
    fun findByTodoId(todoId: String): List<TodoChangeEvent>
    
    // Find change events by uid
    fun findByUid(uid: String): List<TodoChangeEvent>
}

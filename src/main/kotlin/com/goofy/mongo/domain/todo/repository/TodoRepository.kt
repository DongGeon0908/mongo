package com.goofy.mongo.domain.todo.repository

import com.goofy.mongo.domain.todo.entity.Todo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : MongoRepository<Todo, ObjectId>

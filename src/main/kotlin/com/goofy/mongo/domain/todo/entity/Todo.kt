package com.goofy.mongo.domain.todo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "todos")
@CompoundIndexes(
    CompoundIndex(name = "idx__uid__createdAt", def = "{'uid': 1, 'createdAt': 1}")
)
data class Todo(
    @Id
    val id: ObjectId? = null,

    val uid: String,

    var title: String,

    var content: String,

    var completed: Boolean = false,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun update(title: String, content: String, completed: Boolean) {
        this.title = title
        this.content = content
        this.completed = completed
        this.updatedAt = LocalDateTime.now()
    }
}

package com.goofy.mongo.domain.todo.entity

import com.goofy.mongo.domain.base.BaseDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "todos")
@CompoundIndexes(
    CompoundIndex(name = "idx__uid__createdAt", def = "{'uid': 1, 'createdAt': 1}", unique = true)
)
data class Todo(
    val uid: String,

    var title: String,

    var content: String,

    var completed: Boolean = false
) : BaseDocument() {
    fun update(title: String, content: String, completed: Boolean) {
        this.title = title
        this.content = content
        this.completed = completed
        // updatedAt will be automatically updated by Spring Data MongoDB auditing
    }
}

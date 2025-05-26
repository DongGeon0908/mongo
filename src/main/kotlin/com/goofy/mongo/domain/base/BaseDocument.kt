package com.goofy.mongo.domain.base

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

/** 모든 문서 엔티티의 기본 클래스, createdAt과 updatedAt 필드에 대한 감사(auditing) 기능을 제공 */
abstract class BaseDocument {
    /** 문서 ID */
    @Id
    val id: ObjectId? = null

    /** 문서 생성 시간 */
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    /** 문서 마지막 수정 시간 */
    @LastModifiedDate
    @Indexed
    var updatedAt: LocalDateTime = LocalDateTime.now()
}

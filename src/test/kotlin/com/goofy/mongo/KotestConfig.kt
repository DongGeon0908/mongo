package com.goofy.mongo

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringTestExtension

/**
 * Kotest 프로젝트 설정
 * Spring 확장을 활성화하여 Spring Boot 테스트와 통합
 */
class KotestConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(SpringTestExtension())
}

package com.goofy.mongo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.concurrent.TimeUnit

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = ["com.goofy.mongo.domain"])
class MongoConfig : AbstractMongoClientConfiguration() {

    @Value("\${spring.data.mongodb.host}")
    private lateinit var host: String

    @Value("\${spring.data.mongodb.port}")
    private lateinit var port: String

    @Value("\${spring.data.mongodb.database}")
    private lateinit var database: String

    override fun getDatabaseName(): String = database

    @Bean
    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://$host:$port/$database")

        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToConnectionPoolSettings { builder ->
                builder.maxSize(100)
                    .minSize(10)
                    .maxWaitTime(2000, TimeUnit.MILLISECONDS)
                    .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                    .maxConnectionIdleTime(10, TimeUnit.MINUTES)
            }
            .applyToSocketSettings { builder ->
                builder.connectTimeout(2000, TimeUnit.MILLISECONDS)
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
            }
            .build()

        return MongoClients.create(mongoClientSettings)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), databaseName)
    }
}

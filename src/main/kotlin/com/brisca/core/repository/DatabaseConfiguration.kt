package com.brisca.core.repository

import org.davidmoten.rx.jdbc.ConnectionProvider
import org.davidmoten.rx.jdbc.Database
import org.davidmoten.rx.jdbc.pool.Pools
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration() {
//    @Value("\${brisca.database.url}")
//    val url: String? = null
//
//    @Value("\${brisca.database.username}")
//    val username: String? = null
//
//    @Value("\${brisca.database.password}")
//    val password: String? = null
//
//    @Bean
//    fun database(): Database {
//        val connection = ConnectionProvider.from(
//                "$url?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
//                username,
//                password
//        )
//
//        val pool = Pools
//                .nonBlocking()
//                .maxPoolSize(Runtime.getRuntime().availableProcessors() * 4)
//                .connectionProvider(connection)
//                .build()
//
//        return Database.from(pool)
//    }
}
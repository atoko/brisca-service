package com.brisca.core.controller

import com.brisca.core.repository.GameRepository
import com.brisca.core.service.GameService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
class InstrumentationController(
    @Autowired val cosmosClient: GameService,
    @Autowired val objectMapper: ObjectMapper
) {
    companion object {
        @JvmStatic
        final val ok = ResponseEntity.ok().body(mapOf(
                "service" to "brisca-core",
                "status" to "ok"
        ))
    }

    @GetMapping(value=["/.well-known/ok", "/_briscas/.well-known/ok"])
    fun healthCheck(): ResponseEntity<Map<String, String>> {
        return ok
    }

    @GetMapping(value=["/.well-known/db", "/_briscas/.well-known/db"])
    fun databaseCheck(
            @RequestParam path: String
    ): Mono<ResponseEntity<Map<String, String>>> {
        try {
            return cosmosClient.getById(path).map {
                ResponseEntity.ok().body(mapOf(
                        "service" to "cosmos-db",
                        "status" to  ("ok"),
                        "detail" to objectMapper.writeValueAsString(it)
                ))
            }
        } catch (e: Exception) {
            return Mono.just(
                ResponseEntity.ok().body(mapOf(
                        "service" to "cosmos-db",
                        "status" to  "error",
                        "detail" to e.message.orEmpty()
                ))
            )
        }

    }

    @GetMapping(value=["/me", "/_briscas/v1/me"])
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    fun me(principal: Principal): Mono<ResponseEntity<*>> {
        return Mono.just(ResponseEntity.ok().body(
                mapOf( "data" to mapOf(
                    "id" to principal.name
                )
            )
        ))
    }
}

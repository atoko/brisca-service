package com.brisca.core.controller

import com.brisca.core.client.CosmosClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
class InstrumentationController(
    @Autowired val cosmosClient: CosmosClient
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
    fun databaseCheck(): Mono<ResponseEntity<Map<String, String>>> {
        return cosmosClient.healthCheck().map {
            ResponseEntity.ok().body(mapOf(
                "service" to "cosmos-db",
                "status" to  (if (it == true) "ok" else  "error")
            ))
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

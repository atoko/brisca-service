package com.brisca.core.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
class InstrumentationController {
    companion object {
        @JvmStatic
        final val ok = ResponseEntity.ok().body(mapOf(
                "status" to "ok",
                "service" to "brisca-core"
        ))
    }

    @GetMapping(value=["/", "/_briscas"])
    fun healthCheck(): ResponseEntity<Map<String, String>> {
        return ok
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

package com.brisca.core.controller

import com.brisca.core.model.response.GameResponse
import com.brisca.core.model.response.GetResponse
import com.brisca.core.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/_briscas/v1")
class GameQueryController(
        @Autowired val gameService: GameService
) {
    @GetMapping
    public fun healthCheck(): Mono<ResponseEntity<Map<String, String>>> {
        return Mono.just(InstrumentationController.ok)
    }

    @GetMapping("/game/{id}")
    fun getOne(@PathVariable @Valid id: String): Mono<ResponseEntity<GetResponse>> {
        return gameService.getById(id).map {
            ResponseEntity.ok(GetResponse(
                    listOf(GameResponse(
                            it.id.toString(),
                            it.status,
                            it.state
                    ))
            ))
        }
    }
}
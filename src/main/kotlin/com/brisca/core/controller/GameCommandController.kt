package com.brisca.core.controller


import com.brisca.core.model.engine.actions.BotGameAction
import com.brisca.core.model.engine.actions.JoinGameAction
import com.brisca.core.model.engine.actions.PlayCardAction
import com.brisca.core.model.response.GameResponse
import com.brisca.core.model.response.GetResponse
import com.brisca.core.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/_briscas/v1")
class GameCommandController(
        @Autowired val gameService: GameService
) {

    @PostMapping("game", consumes=[APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    fun create(principal: Principal): Mono<ResponseEntity<GetResponse>> {
        val playerId = principal.name
        return gameService.post(playerId).map {
            ResponseEntity.ok(GetResponse(
                    listOf(GameResponse(
                            it.id.toString(),
                            it.status,
                            it.state
                    ))
            ))
        }
    }

    @PostMapping("/game/{id}/join", consumes=[APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    fun join(principal: Principal, @PathVariable @Valid id: String): Mono<ResponseEntity<GetResponse>> {
        val playerId = principal.name
        return gameService.command(id, JoinGameAction(playerId)).map {
            ResponseEntity.ok(GetResponse(
                    listOf(GameResponse(
                            it.id.toString(),
                            it.status,
                            it.state
                    ))
            ))
        }
    }

    @PostMapping("/game/{id}/bot", consumes=[APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    fun bot(principal: Principal, @PathVariable @Valid id: String): Mono<ResponseEntity<GetResponse>> {
        val playerId = principal.name
        return gameService.command(id, BotGameAction(playerId)).map {
            ResponseEntity.ok(GetResponse(
                    listOf(GameResponse(
                            it.id.toString(),
                            it.status,
                            it.state
                    ))
            ))
        }
    }

    @PostMapping("/game/{id}/card/{card}", consumes=[APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('ROLE_PLAYER')")
    fun card(principal: Principal, @PathVariable @Valid id: String, @PathVariable @Valid card: Int): Mono<ResponseEntity<GetResponse>> {
        val playerId = principal.name
        return gameService.command(id, PlayCardAction(playerId, card)).map {
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
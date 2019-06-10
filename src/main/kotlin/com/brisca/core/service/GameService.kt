package com.brisca.core.service

import com.brisca.core.model.domain.GameData
import com.brisca.core.model.engine.*
import com.brisca.core.model.engine.actions.Action
import com.brisca.core.model.engine.actions.JoinGameAction
import com.brisca.core.model.engine.actions.NewGameAction
import com.brisca.core.model.engine.actions.PlayCardAction
import com.brisca.core.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GameService(
        @Autowired val gameRepository: GameRepository
) {
    fun getById(id: String): Mono<GameData> {
        return gameRepository.loadOne(id).map {
            GameData(it)
        }
    }
    fun post(playerId: String): Mono<GameData>{
        val data = GameData(
                status = "CREATED",
                state = Reducer.game(GameState(), NewGameAction((0..39).shuffled().toList(), 2))
        ).apply {
            this.state = Reducer.game(this.state, JoinGameAction(playerId.toLong()))
        }

        return gameRepository.saveOne(data).map {
            GameData(it)
        }
    }
    fun command(id: String, action: Action): Mono<GameData> {
        return gameRepository.loadOne(id).flatMap {
            var idempotencyKey = -1
            val data = GameData(it).also { gameData ->
                idempotencyKey = gameData.state.idempotency
                gameData.state = Reducer.game(gameData.state, action)
                gameData.status = when (action) {
                    is JoinGameAction -> {
                        when {
                            gameData.state.tableSize == gameData.state.players.count() -> "FULL"
                            else -> "WAITING"
                        }
                    }
                    is PlayCardAction -> {
                        when {
                            GameState.isGameEnded(gameData.state) -> "GAME"
                            GameState.isRoundEnded(gameData.state) -> "ROUND"
                            else -> "PLAYING"
                        }
                    }
                    else -> gameData.status
                }

            }

            if (idempotencyKey != data.state.idempotency) {
                gameRepository.saveOne(data).map {
                    GameData(it)
                }
            } else {
                Mono.just(GameData(it))
            }
        }
    }
}
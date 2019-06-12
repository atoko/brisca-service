package com.brisca.core.repository

import com.brisca.core.model.beans.GameRow
import com.brisca.core.model.domain.GameData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class GameRepository() {
    companion object {
        const val GAME_API_VERSION = "1"
    }

    private val games : MutableMap<String, GameRow> = mutableMapOf()

    fun loadOne(id: String?): Mono<GameRow> {
        if (id != null) {
            if (!games.containsKey(id)) {
                throw RuntimeException("Game ($id) not found")
            }
            return Mono.just(games.get(id)!!)
        }

        return Mono.just(GameRow().apply {
            this.id = (Long.MAX_VALUE - 10010001900).toString()
        })
    }

    fun saveOne(gameData: GameData): Mono<GameRow> {
        val id = UUID.randomUUID().toString()
        val row = GameRow(gameData).apply {
            val date = if (gameData.id == null) this.createdAt else Date()
            this.id = gameData.id ?: id
            this.modified = date
            games.put(id, this)
        }

        return Mono.just(row)
    }
}
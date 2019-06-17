package com.brisca.core.repository

import com.brisca.core.model.beans.GameByIdRow
import com.brisca.core.model.domain.GameData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

//@Component
//class GameRepository() {
//    companion object {
//        const val GAME_API_VERSION = "1"
//    }
//
//    private val games : MutableMap<String, GameByIdRow> = mutableMapOf()
//
//    fun loadOne(id: String?): Mono<GameByIdRow> {
//        if (id != null) {
//            if (!games.containsKey(id)) {
//                throw RuntimeException("Game ($id) not found")
//            }
//            return Mono.just(games.get(id)!!)
//        }
//
//        return Mono.just(GameByIdRow().apply {
//            this.id = (Long.MAX_VALUE - 10010001900).toString()
//        })
//    }
//
//    fun saveOne(gameData: GameData): Mono<GameByIdRow> {
//        val id = UUID.randomUUID().toString()
//        val row = GameByIdRow(gameData).apply {
//            val date = if (gameData.id == null) this.createdAt else Date()
//            this.id = gameData.id ?: id
//            this.modified = date
//            games.put(id, this)
//        }
//
//        return Mono.just(row)
//    }
//}
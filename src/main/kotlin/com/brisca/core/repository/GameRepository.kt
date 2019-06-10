package com.brisca.core.repository

import com.brisca.core.model.beans.GameRow
import com.brisca.core.model.beans.IGameRow
import com.brisca.core.model.domain.GameData
import org.davidmoten.rx.jdbc.Database
import org.postgresql.util.PGobject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.adapter.rxjava.RxJava2Adapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.RuntimeException
import java.util.*
import java.util.stream.Collectors
//
//@Component
//class GameRepository(
//        @Autowired val database: Database
//) {
//    companion object {
//        const val GAME_API_VERSION = "1"
//    }
//
//    fun loadOne(id: String?): Mono<GameRow> {
//        if (id != null) {
//            return RxJava2Adapter.flowableToFlux(database.select(
//                    "SELECT * FROM brisca.games WHERE id=?"
//            ).parameter(id.toLong())
//            .autoMap(IGameRow::class.java))
//            .collect(Collectors.toList())
//            .map {
//                if (it.isEmpty()) {
//                    throw RuntimeException("Game ($id) not found")
//                } else {
//                    GameRow(it.get(0))
//                }
//            }
//
//        }
//
//        return Mono.just(GameRow().apply {
//            this.id = (Long.MAX_VALUE - 10010001900).toString()
//        })
//    }
//
//    fun saveOne(gameData: GameData): Mono<GameRow> {
//        val query = if (gameData.id == null) "INSERT INTO brisca.games(data, created_at, version) VALUES (?, ?, ?)"
//            else "UPDATE brisca.games SET data=?, modified=? WHERE id=?"
//        val row = GameRow(gameData)
//        val date = if (gameData.id == null) row.createdAt else Date()
//
//        val data = PGobject().apply {
//            this.type = "jsonb"
//            this.value = row.rawData
//        }
//        val parameters = mutableListOf(data, date)
//        if (gameData.id != null) {
//            parameters.add(gameData.id.toLong())
//        } else {
//            parameters.add(GAME_API_VERSION)
//        }
//
//        return RxJava2Adapter.flowableToFlux(database.update(query)
//            .parameters(parameters)
//            .returnGeneratedKeys()
//            .getAsOptional(Long::class.java)
//        )
//            .doOnNext { id ->
//                if (id.isEmpty) {
//                    throw RuntimeException("Error saving game to database")
//                }
//            }
//            .map {l ->
//                row.apply {
//                    this.id = l.get().toString()
//                }
//            }
//            .collect(Collectors.toList())
//            .map { it.get(0) }
//    }
//}
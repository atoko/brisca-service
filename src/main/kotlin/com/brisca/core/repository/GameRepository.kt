package com.brisca.core.repository

import com.brisca.core.model.beans.GameByIdRow
import com.brisca.core.model.domain.GameData
import com.microsoft.azure.spring.data.cosmosdb.repository.DocumentDbRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository : DocumentDbRepository<GameByIdRow, String> {
    fun findByGameId(gameId: String): List<GameByIdRow>
    fun save(row: GameByIdRow): GameByIdRow

    companion object {
        fun generateRow(gameData: GameData): GameByIdRow {
        val id = UUID.randomUUID().toString()
        val row = GameByIdRow(gameData).apply {
            this.gameId = gameData.id ?: id
        }

            return row
        }
    }
}
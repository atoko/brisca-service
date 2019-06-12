package com.brisca.core.model.response

import com.brisca.core.model.domain.GameData
import com.brisca.core.model.engine.Card
import com.brisca.core.model.engine.GameState
import com.brisca.core.model.engine.PlayerState

data class GameResponse(
        val id: String,
        val status: String,
        val lifeCard: Int?,
        val players: Map<String, PlayerResponse>,
        val rounds: Iterable<Any>,
        val tableOwner: String?,
        val nextToPlay: String?,
        val table: Iterable<Card>,
        val lastTable: Iterable<Card>,
        val cardsLeft: Int,
        val roundEnded: Boolean,
        val gameEnded: Boolean
) {
    constructor() : this(
            "",
            "",
            null,
            emptyMap<String, PlayerResponse>(),
            emptyList<Any>(),
            null,
            null,
            emptyList<Card>(),
            emptyList<Card>(),
            0,
            false,
            false
    )
    public constructor(id: String, status: String, state: GameState) : this(
        id,
        status,
        state.life,
        state.players.map { p -> p.key.toString() to PlayerResponse(p.value) }.toMap(),
        state.rounds,
        if (state.tableOwner != null) state.tableOwner.toString() else null,
        if (state.nextToPlay() != null) state.nextToPlay().toString() else null,
        state.table,
        state.lastTable,
        state.deck.count(),
        GameState.isRoundEnded(state),
        GameState.isGameEnded(state)
    )
}
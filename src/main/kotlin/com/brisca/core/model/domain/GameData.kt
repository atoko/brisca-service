package com.brisca.core.model.domain

import com.brisca.core.model.beans.GameRow
import com.brisca.core.model.engine.GameState
import com.fasterxml.jackson.annotation.JsonIgnore

data class GameData(
        @field:JsonIgnore
        val id: String? = null,
        var status: String = "MEMORY",
        var state: GameState = GameState.default
) {
    constructor(row: GameRow) : this(row.id, row.data!!.status, row.data!!.state)
}
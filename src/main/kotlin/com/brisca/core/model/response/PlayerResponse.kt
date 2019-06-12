package com.brisca.core.model.response

import com.brisca.core.model.engine.PlayerState
import com.fasterxml.jackson.annotation.JsonIgnore

data class PlayerResponse(
        @field:JsonIgnore
        val id: String,
        val next: String,
        val hand: MutableCollection<Int>,
        val points: Int) {
    constructor() : this(
            "",
            "",
            mutableListOf(),
            0
    )
    constructor(player: PlayerState) : this(
            player.id.toString(),
            player.next.toString(),
            player.hand,
            player.points
    )

}

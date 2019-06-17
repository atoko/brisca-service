package com.brisca.core.model.engine

import java.util.*
import kotlin.random.Random

data class PlayerState(
        var id: String?,
        var next: String?,
        var isBot: Boolean,
        val hand: MutableCollection<Int> = mutableSetOf(),
        var points: Int = 0
) {
    constructor(): this(defaultId, null, false)
    constructor(playerId: String?) : this(playerId, null, false) {
        if (id == null) {
            this.id = UUID.randomUUID().also {
                UUID(it.mostSignificantBits, 0xCAFE)
            }.toString()
            this.isBot = true
        }
    }

    companion object {
        const val defaultId: String = ""
    }

    fun drawCard(card: Int): PlayerState {
        hand.add(card)
        return this
    }

    fun playCard(card: Int): PlayerState {
        hand.remove(card)
        return this
    }

    fun addPoints(points: Int): PlayerState {
        this.points += points
        return this
    }
}
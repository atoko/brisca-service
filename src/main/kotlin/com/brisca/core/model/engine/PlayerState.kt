package com.brisca.core.model.engine

import kotlin.random.Random

data class PlayerState(
        var id: Long?,
        var next: Long?,
        var isBot: Boolean,
        val hand: MutableCollection<Int> = mutableSetOf(),
        var points: Int = 0
) {
    constructor(): this(defaultId, null, false)
    constructor(playerId: String): this(playerId.toLong(), null, false)
    constructor(playerId: Long?) : this(playerId, null, false) {
        if (id == null) {
            this.id = Random.nextLong()
            this.isBot = true
        }
    }

    companion object {
        const val defaultId: Long = Int.MAX_VALUE.toLong()
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
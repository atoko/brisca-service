package com.brisca.core.model.engine.actions

data class PlayCardAction(val playerId: Long, val card: Int) : Action()
package com.brisca.core.model.engine.actions

data class PlayCardAction(val playerId: String, val card: Int) : Action()
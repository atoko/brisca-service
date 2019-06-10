package com.brisca.core.model.engine.actions

data class NewGameAction(val deck: Collection<Int>, val tableSize: Int) : Action()
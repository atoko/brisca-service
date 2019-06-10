package com.brisca.core.model.engine

import com.brisca.core.model.engine.actions.*
import java.util.*

abstract class Reducer() {
    companion object {
        @JvmStatic
        fun game(state: GameState, action: Action = Action.noop): GameState {
            when (action) {
                is NewGameAction ->
                    state.newGame(action.deck, action.tableSize)
                is BotGameAction -> {
                    if (action.playerId == state.tableOwner) {
                        state.joinGame(null)
                            .startGameTransition(BriscaParameters.instance)
                    }
                }
                is JoinGameAction -> {
                    state.joinGame(action.playerId)
                        .startGameTransition(BriscaParameters.instance)
                }
                is PlayCardAction -> {
                    state.playCard(action.playerId, action.card)
                        .afterPlayTransition(BriscaParameters.instance)
                }
            }

            return state
        }
    }
}
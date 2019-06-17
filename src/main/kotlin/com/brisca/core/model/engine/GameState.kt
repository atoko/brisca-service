package com.brisca.core.model.engine

import com.brisca.core.model.response.GameResponse
import java.util.*

data class GameState(
        val deck: Deque<Int> = ArrayDeque(),
        val rounds: Deque<Int> = ArrayDeque(),
        var tableSize: Int = 0,
        var tableOwner: String? = null,
        var life: Int? = null,
        val players: MutableMap<String, PlayerState> = mutableMapOf(),
        val table: Deque<Card> = ArrayDeque(),
        val lastTable: Deque<Card> = ArrayDeque(),
        val turnSequence: Deque<String> = ArrayDeque(),
        var idempotency: Int = 0
) {
    companion object {
        val default: GameState = GameState()
        public fun isRoundEnded(state: GameState): Boolean {
            return state.deck.count().let {
                val playerCardCount: Int = state.players.values
                        .toList()
                        .foldRight(0) { c, acc ->
                            acc + c.hand.count()
                        }

                it + playerCardCount == 0
            }
        }

        public fun isGameEnded(state: GameState): Boolean {
            return state.deck.count().let {
                val playerCardCount: Int = state.players.values
                        .toList()
                        .foldRight(0) { c, acc ->
                            acc + c.hand.count()
                        }

                //TODO proper round counting logic
                val roundCount: Int = state.rounds.count()

                it + playerCardCount == 0 && roundCount > 0
            }
        }
    }

    @Deprecated("Unit test only")
    public fun readState(): GameResponse {
        val playerCardCount: Int = players.values
                .toList()
                .foldRight(0) { c, acc ->
                    acc + c.hand.count()
                }

        return GameResponse(
            "",
            "",
            this
        )
    }

    fun newGame(deck: Collection<Int>, tableSize: Int): GameState {
        this.deck.addAll(deck)
        this.life = deck.last()
        this.tableSize = tableSize

        return this
    }

    fun joinGame(playerId: String?): GameState {
        val playerState = PlayerState(playerId)
        var id = playerState.id


                //Full
        if (players.count() >= tableSize) {
            return this
        }

        //Already joined
        if (players.containsKey(id.toString())) {
            return this
        }
        players[id.toString()] = playerState

        if (tableOwner == null) {
            tableOwner = id
        }
        turnSequence.addLast(id)

        val playerCount = players.count()
        players.forEach { player ->
            player.value.also { ps ->
                val index = turnSequence.indexOf(ps.id)
                ps.next = if (index + 1 >= playerCount) tableOwner else turnSequence.elementAt(index + 1)
            }
        }
        idempotency++
        return this
    }

    fun startGameTransition(parameters: Parameters): GameState {
        if ((players.count() == tableSize && deck.count() >= parameters.totalCards)) {
            for (i in 1..parameters.handSize) {
                for (player in turnSequence.iterator()) {
                    val card = deck.pop()
                    players[player.toString()]?.drawCard(card)
                }
            }
        }

        return this
    }


    fun playCard(playerId: String, card: Int): GameState {
        val nextPlayer = nextToPlay()

        //Not enough players
        if (players.count() < tableSize) {
            return this
        }

        if (players[nextPlayer.toString()]?.hand?.contains(card) == true) {
            if (nextPlayer == playerId) {
                players[nextPlayer.toString()]?.playCard(card)
                table.push(Card(card, playerId))
                turnSequence.pop()
                idempotency++
            }
        }

        return this
    }

    fun afterPlayTransition(parameters: Parameters): GameState {
        val tableCount = table.count()
        if (tableCount > 0 && tableCount % tableSize == 0) {
            var winCard: Card? = null
            var points = 0

            turnSequence.clear()
            lastTable.clear()

            do {
                val card = table.pop()
                points += card.getPoints(parameters)
                lastTable.push(card)
                if (winCard == null) {
                    winCard = card
                }
                winCard = Card.determineWinner(parameters, life!!, winCard!!, card).card
            }
            while(table.isNotEmpty())

            players[winCard?.playerId.toString()]?.addPoints(points)

            //Establish next turn sequence starting with winner
            var player = players[winCard?.playerId.toString()]
            for (i in 1..tableSize) {
                turnSequence.addLast(player?.id)
                if (deck.isNotEmpty()) {
                    player?.drawCard(deck.pop())
                }
                player = players[player?.next.toString()]
            }
        }
        return this
    }

    public fun nextToPlay(): String? {
        if (turnSequence.isNotEmpty()) {
            return turnSequence.peekFirst()
        }
        return null
    }

}
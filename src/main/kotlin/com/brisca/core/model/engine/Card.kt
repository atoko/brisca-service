package com.brisca.core.model.engine


data class Card(
        val index: Int,
        val playerId: Long
) {
    enum class Suit(floorIndex: Int) {
        STAFF(0),
        SWORD(1),
        COIN(2),
        CUP(3),
        UNKNOWN(4);

        val floorIndex: Int = floorIndex
    }

    fun getPoints(parameters: Parameters): Int {
        return determinePointValue(parameters, this)
    }

    constructor() : this(-1, -1)

    companion object {
        fun determineSuit(card: Card): Suit {
            return determineSuit(card.index)
        }

        fun determineSuit(index: Int): Suit {
            val floor = Math.floor(index.toDouble() / 10).toInt()
            return when (floor) {
                0 -> Suit.STAFF
                1 -> Suit.SWORD
                2 -> Suit.COIN
                3 -> Suit.CUP
                else -> Suit.UNKNOWN
            }
        }

        fun determineFace(card: Card): Int {
            return card.index - (determineSuit(card).floorIndex * 10)
        }

        fun determineLife(life: Int, card: Card): Boolean {
            return determineSuit(life).floorIndex == determineSuit(card).floorIndex
        }

        fun determinePointValue(parameters: Parameters, card: Card): Int {
            return parameters.pointMapping[determineFace(card)]?: 0
        }

        fun determineWinner(parameters: Parameters, life: Int, a: Card, b: Card): CardResult {
            var winner = CardResult(a, "DEFAULT")

            if (a.index == b.index) {
                return winner
            }

            if (!determineLife(life, a) && determineLife(life, b)) {
                winner = winner.apply {
                    this.card = b
                    this.reason = "TRUMP"
                }
            }

            if (determineSuit(b).floorIndex == determineSuit(a).floorIndex) {
                if (determinePointValue(parameters, b) > determinePointValue(parameters, a)) {
                    winner = winner.apply {
                        this.card = b
                        this.reason = "POINTS"
                    }
                }

                if (determinePointValue(parameters, a) == determinePointValue(parameters, b)) {
                    if(determineFace(a) > determineFace(b)) {
                        winner = winner.apply {
                            this.card = a
                            this.reason = "FACE_VALUE"
                        }
                    } else {
                        winner = winner.apply {
                            this.card = b
                            this.reason = "FACE_VALUE"
                        }
                    }
                }
            }

            return winner
        }
    }

    data class CardResult(var card: Card, var reason: String)
}

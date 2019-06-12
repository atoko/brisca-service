package com.brisca.core.model.engine

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("unit")
class BriscaCardTest {
    private val instance: BriscaParameters = BriscaParameters.instance

    @Test
    fun `life_wins_hand`() {
        (0 until instance.totalCards step 10).forEach { suit ->
            val suitCards = (suit until suit + 10)
            val restCards = (0 until instance.totalCards).filter { !suitCards.contains(it) }

            restCards.forEach { card ->
                suitCards.forEach { suitCard ->
                    assertEquals(Card.determineWinner(
                            instance,
                            suitCard,
                            Card(card, 0),
                            Card(suitCard, 1)
                    ).card.index, suitCard)

                    assertEquals(Card.determineWinner(
                            instance,
                            suitCard,
                            Card(suitCard, 0),
                            Card(card, 1)
                    ).card.index, suitCard)
                }
            }
        }
    }

    @Test
    fun `game_has_120_points`() {
        var totalPoints: Int = 0
        (0 until instance.totalCards).forEach { card ->
            totalPoints += Card(card, 0).getPoints(BriscaParameters.instance)
        }

        assertEquals(120, totalPoints)
    }

    @Test
    fun `first_card_wins_if_suits_different`() {
        (0 until instance.totalCards step 10).forEach { suit ->
            val suitCards = (suit until suit + 10)
            val restCards = (0 until instance.totalCards).filter { !suitCards.contains(it) }

            restCards.forEach { card ->
                suitCards.forEach { suitCard ->
                    val winner = if (Card.determineLife(restCards.last(), Card(card, 0))) card else suitCard
                    assertEquals(Card.determineWinner(
                            instance,
                            restCards.last(),
                            Card(suitCard, 0),
                            Card(card, 1)
                    ).card.index, winner)
                }
            }
        }
    }

    @Test
    fun `points_wins_numeric_higher`() {
        (0 until instance.totalCards step 10).forEach { suitAce ->
            val suitThree = suitAce + 2
            val sansAce = (suitAce until suitAce + 10).filter { it != suitAce }
            val sansThree = (suitAce until suitAce + 10).filter { it != suitThree }

            sansAce.forEach{suitCard ->
                assertEquals(
                    Card.determineWinner(
                        instance,
                        suitAce,
                        Card(suitCard, 0),
                        Card(suitAce, 0)
                    ).card.index,
                    suitAce
                )
            }

            sansThree.forEach{ suitCard: Int ->
                val winner = if (suitCard == suitAce) suitAce else suitThree
                assertEquals(
                        Card.determineWinner(
                                instance,
                                suitAce,
                                Card(suitCard, 0),
                                Card(suitThree, 0)
                        ).card.index,
                        winner
                )
            }
        }
    }
}
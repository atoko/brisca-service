package com.brisca.core.model.engine

import com.brisca.core.model.engine.actions.JoinGameAction
import com.brisca.core.model.engine.actions.NewGameAction
import com.brisca.core.model.engine.actions.PlayCardAction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.lang.Long.parseLong

@Tag("unit")
class ReducerTest {
    @Test
    fun `it_can_initialize_deck_from_array`() {
        var state = GameState()
        val action = NewGameAction((0..39).map { i -> i }, 2)
        val get = Reducer.game(state, action).readState()

        Assertions.assertEquals(BriscaParameters.instance.totalCards, get.cardsLeft)
        Assertions.assertEquals(39, get.lifeCard)
    }

    @Test
    fun `when_joined_cards_are_dealt`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("111"),
                JoinGameAction("222")
        )
        actions.forEach{
            state = Reducer.game(state, it)
        }
        val get = state.readState()

        Assertions.assertEquals(BriscaParameters.instance.totalCards - (BriscaParameters.instance.handSize*2),
                get.cardsLeft
        )
        Assertions.assertEquals("111", get.tableOwner)
        Assertions.assertEquals(BriscaParameters.instance.handSize, get.players.values.find { p -> p.id.toString() == get.tableOwner }?.hand?.count())
        Assertions.assertEquals("222", get.players.values.find { p -> p.id == "111" }?.next)
        Assertions.assertEquals("111", get.players.values.find { p -> p.id == "222" }?.next)
    }

    @Test
    fun `cant_join_table_when_full`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("1"),
                JoinGameAction("2"),
                JoinGameAction("3")
        )
        actions.forEach{
            state = Reducer.game(state, it)
        }
        val get = state.readState()

        Assertions.assertEquals(
                BriscaParameters.instance.totalCards - (BriscaParameters.instance.handSize*2),
                get.cardsLeft
        )
        Assertions.assertEquals("1", get.tableOwner)
        Assertions.assertEquals(2, get.players.count())
    }

    @Test
    fun `cannot_play_when_table_not_full`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("1"),
                PlayCardAction("1", 32)
        )
        actions.forEach{
            state = Reducer.game(state, it)
        }
        val get = state.readState()

        Assertions.assertEquals(BriscaParameters.instance.totalCards, get.cardsLeft)
        Assertions.assertEquals("1", get.tableOwner)
        Assertions.assertEquals(0, get.table.count())
    }

    @Test
    fun `winner_has_next_turn`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("1"),
                JoinGameAction("33"),
                PlayCardAction("1", 0),
                PlayCardAction("33", 3)
        )

        actions.forEach{
            state = Reducer.game(state, it)
        }
        val get = state.readState()

        Assertions.assertEquals(BriscaParameters.instance.handSize, get.players.values.find { p -> p.id.toString() == "1" }?.hand?.count())
        Assertions.assertEquals(BriscaParameters.instance.handSize, get.players.values.find { p -> p.id.toString() == "33" }?.hand?.count())
        Assertions.assertEquals("1", get.nextToPlay)
        Assertions.assertEquals("1", get.tableOwner)
        Assertions.assertEquals(0, get.table.count())
    }

    @Test
    fun `last_play_selector`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("11"),
                JoinGameAction("33"),
                PlayCardAction("11", 0),
                PlayCardAction("33", 3)
        )

        actions.forEach{
            state = Reducer.game(state, it)
        }
        var get = state.readState()
        Assertions.assertEquals(2, get.lastTable.count())
        Assertions.assertEquals(listOf(0, 3).toString(), get.lastTable.map{ c -> c.index }.toString())

        state = Reducer.game(state, PlayCardAction("11", 2))
        get = state.readState()
        Assertions.assertEquals(2, get.lastTable.count())
        Assertions.assertEquals(listOf(0, 3).toString(), get.lastTable.map{ c -> c.index }.toString())

        state = Reducer.game(state, PlayCardAction("33", 1))
        get = state.readState()
        Assertions.assertEquals(2, get.lastTable.count())
        Assertions.assertEquals(listOf(2, 1).toString(), get.lastTable.map{ c -> c.index }.toString())
    }

    @Test
    fun `cannot_play_card_in_deck`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("1"),
                JoinGameAction("33"),
                PlayCardAction("1", 39)
        )

        actions.forEach{
            state = Reducer.game(state, it)
        }
        val get = state.readState()

        Assertions.assertEquals(0, get.table.count())
        Assertions.assertEquals(BriscaParameters.instance.handSize, get.players.values.find { p -> p.id.toString() == "1" }?.hand?.count())
        Assertions.assertEquals(BriscaParameters.instance.handSize, get.players.values.find { p -> p.id.toString() == "33" }?.hand?.count())
        Assertions.assertEquals("1", get.nextToPlay)
        Assertions.assertEquals("1", get.tableOwner)
    }
    @Test
    fun `players_hands_empty_after_all_cards_played`() {
        var state = GameState()
        val actions = listOf(
                NewGameAction((0..39).map { i -> i }, 2),
                JoinGameAction("11"),
                JoinGameAction("33")
        )
        actions.forEach{
            state = Reducer.game(state, it)
        }

        var get = state.readState()
        for(i in 0 until BriscaParameters.instance.totalCards) {
            val nextPlayer = get.players.values.single { p -> p.id == get.nextToPlay }
            state = Reducer.game(state, PlayCardAction(get.nextToPlay.orEmpty(), nextPlayer.hand.first()))
            get = state.readState()
        }

        Assertions.assertEquals(0, get.players.values.find { p -> p.id == "11" }?.hand?.count())
        Assertions.assertEquals(0, get.players.values.find { p -> p.id == "33" }?.hand?.count())
        Assertions.assertEquals(true, get.roundEnded)
        Assertions.assertEquals(0, get.cardsLeft)
    }
}
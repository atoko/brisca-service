package com.brisca.core.integration

import com.brisca.core.model.response.GetResponse
import com.brisca.core.model.response.PlayerResponse
import com.brisca.core.security.model.Role
import com.brisca.core.security.model.User
import com.brisca.core.util.JwtTools
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import java.lang.RuntimeException
import java.net.URI
import java.util.function.Consumer

@Tag("integration")
@SpringBootTest
@AutoConfigureWebFlux
@AutoConfigureWebTestClient
class GameIntegrationTest(
    @Autowired val webTestClient: WebTestClient,
    @Autowired val jwtTools: JwtTools
) {
    var playerOne = ""
    var playerTwo = ""
    var playerMap: Map<String, String>? = null

    val objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        playerOne = jwtTools.generateToken(User("100", "", listOf(Role.ROLE_PLAYER)))
        playerTwo = jwtTools.generateToken(User("5000", "", listOf(Role.ROLE_PLAYER)))
        playerMap = mapOf(
                "100" to playerOne,
                "5000" to playerTwo
        )
    }

    @Test
    fun `post_creates_one_game`() {
        webTestClient.post()
                .uri(URI.create("/_briscas/v1/game"))
                .header("Authorization", "Bearer $playerOne")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("""{"data": [{"cardsLeft":40, "players": {"100": {}}}]}""")
    }

    @Test
    fun `post_created_game_can_be_retrieved`() {
            webTestClient.post()
                    .uri(URI.create("/_briscas/v1/game"))
                    .header("Authorization", "Bearer $playerOne")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectBody()
                    .json("""{"data": [{"players": {"100": {}}}]}""")
                    .consumeWith {
                        val response = objectMapper.readValue<GetResponse>(it.responseBody!!)
                        val id = response.data.get(0).id
                        webTestClient.get()
                                .uri(URI.create("/_briscas/v1/game/$id"))
                                .header("Authorization", "Bearer $playerOne")
                                .exchange()
                                .expectBody()
                                .json("""{"data": [{"id": "$id"}]}""")
                    }
    }

    @Test
    fun `can_join_game`() {
        webTestClient.post()
                .uri(URI.create("/_briscas/v1/game"))
                .header("Authorization", "Bearer $playerOne")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("""{"data": [{"players": {"100": {}}}]}""")
                .consumeWith(joinGame(playerTwo))
    }

    @Test
    fun `cant_add_bot_to_someone_elses_game`() {
        webTestClient.post()
                .uri(URI.create("/_briscas/v1/game"))
                .header("Authorization", "Bearer $playerOne")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("""{"data": [{"players": {"100": {}}}]}""")
                .consumeWith(botGame(playerTwo))
    }

    @Test
    fun `can_add_bot`() {
        webTestClient.post()
                .uri(URI.create("/_briscas/v1/game"))
                .header("Authorization", "Bearer $playerOne")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("""{"data": [{"players": {"100": {}}}]}""")
                .consumeWith(botGame(playerOne, Consumer {
                    //verify deck is 36
                    //verify both players
                    null
                }))
    }

    @Test
    fun `round_ended_works_as_expected`() {
        webTestClient.post()
                .uri(URI.create("/_briscas/v1/game"))
                .header("Authorization", "Bearer $playerOne")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("""{
                    "data": [{
                        "players": {
                            "100": {}
                        },
                        "roundEnded": false,
                        "gameEnded": false
                    }]
                }""")
                .consumeWith(joinGame(playerTwo, playRound(Consumer {
                    val response = objectMapper.readValue<GetResponse>(it.responseBody!!)
                    val game = response.data[0]

                    Assertions.assertEquals(true, game.roundEnded)
                })))
    }

    private fun joinGame(token: String, chainWith: Consumer<EntityExchangeResult<ByteArray>>? = null): Consumer<EntityExchangeResult<ByteArray>> {
        return Consumer {
            val response = objectMapper.readValue<GetResponse>(it.responseBody!!)
            val id = response.data.get(0).id
            val action = webTestClient.post()
                    .uri(URI.create("/_briscas/v1/game/$id/join"))
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectBody()
                    .json("""{"data": [{
                            "id": "$id",
                            "players": {
                                "100": {},
                                "5000": {}
                            },
                            "cardsLeft": 34
                        }]}""")

            if (chainWith != null) {
                action.consumeWith(chainWith)
            }
        }
    }
    private fun botGame(token: String, chainWith: Consumer<EntityExchangeResult<ByteArray>>? = null): Consumer<EntityExchangeResult<ByteArray>> {
        return Consumer {
            val response = objectMapper.readValue<GetResponse>(it.responseBody!!)
            val id = response.data.get(0).id
            val action = webTestClient.post()
                    .uri(URI.create("/_briscas/v1/game/$id/bot"))
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectBody()
                    .json("""{"data": [{
                            "id": "$id"
                        }]}""")


            if (chainWith != null) {
                action.consumeWith(chainWith)
            }
        }
    }
    private fun playRound(chainWith: Consumer<EntityExchangeResult<ByteArray>>? = null): Consumer<EntityExchangeResult<ByteArray>> {
        return Consumer {
            val response = objectMapper.readValue<GetResponse>(it.responseBody!!)
            val game = response.data[0]
            val id = game.id
            val token = playerMap?.get(game.nextToPlay)

            if (!game.roundEnded) {
                val card = game.players[game.nextToPlay]?.hand?.firstOrNull()
                webTestClient.post()
                        .uri(URI.create("/_briscas/v1/game/$id/card/$card"))
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectBody()
                        .json("""{"data": [{
                            "id": "$id",
                            "players": {
                                "100": {},
                                "5000": {}
                            }
                        }]}""")
                        .consumeWith(playRound())
            } else {
                val totalPoints = game.players.values.fold(0) { acc, current ->
                    acc + current.points
                }

                Assertions.assertEquals(0, game.cardsLeft)
                Assertions.assertEquals(120, totalPoints)

                chainWith?.accept(it)
            }
        }
    }
}

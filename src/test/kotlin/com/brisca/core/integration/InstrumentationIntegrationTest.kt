package com.brisca.core.integration

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.config.EnableWebFlux
import java.net.URI

@Tag("integration")
@SpringBootTest
@AutoConfigureWebFlux
@AutoConfigureWebTestClient
class InstrumentationIntegrationTest(
    @Autowired val webTestClient: WebTestClient
) {
    @Test
    @WithMockUser(authorities = ["ROLE_PLAYER"], username = "4269")
    fun `can_get_details_on_logged_in_principal`() {
        webTestClient.get()
                .uri(URI.create("/_briscas/v1/me"))
                .exchange()
                .expectBody()
                .json("""{"data": {"id": "4269"}}""")
    }
}
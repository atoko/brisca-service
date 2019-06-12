package com.brisca.core.client

import com.brisca.core.config.CosmosConfig
import com.brisca.core.util.CircuitBreakerFactory
import com.brisca.core.util.CosmosSignatureCache
import com.brisca.core.util.WebClientFactory
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Component
class CosmosClient(
    @Autowired webClientFactory: WebClientFactory,
    @Autowired val config: CosmosConfig,
    @Autowired circuitBreakerRegistry: CircuitBreakerRegistry,
    @Autowired val circuitBreakerFactory: CircuitBreakerFactory,
    @Autowired val cosmosSignatureCache: CosmosSignatureCache

) {
    private val webClient = webClientFactory.create(config)
    private val rfc1123: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    private val urlEncoding = StandardCharsets.UTF_8.toString()
    private val circuitBreaker = circuitBreakerRegistry.circuitBreaker("cosmos")
    private val appName = "brisca-service"
    private val tokenVersion = "1.0"

    public fun healthCheck() : Mono<Boolean> {
        return post("docs", "", "")
            .retrieve()
            .bodyToMono(Object::class.java)
            .transform(circuitBreakerFactory.get(circuitBreaker))
            .map { cosmosResponse ->
                false
            }
    }

    public fun queryDocuments(path: String, sql: String): Flux<Any> {
        return post("docs", path, "")
            .retrieve()
            .bodyToMono(Object::class.java)
            .transform(circuitBreakerFactory.get(circuitBreaker))
            .flatMapMany { cosmosResponse ->
                Flux.fromIterable(listOf(""))
            }
    }

    private fun get(type: String, path: String): WebClient.RequestHeadersSpec<*> {
        val date = rfc1123.format(Date()).toLowerCase()
        val signature = cosmosSignatureCache.retrieve("get", type, path, date)
        val authorization = "type=${config.svcId}&ver=$tokenVersion&sig=$signature"

        return webClient.get()
            .header("x-ms-version", "2018-12-31")
            .header("x-ms-date", date)
            .header("Authorization", URLEncoder.encode(authorization, urlEncoding))
            .header("Content-Type", "application/json")
    }

    private fun post(type:String, path: String, body: Any): WebClient.RequestHeadersSpec<*> {
        val date = rfc1123.format(Date()).toLowerCase()
        val signature = cosmosSignatureCache.retrieve("post", type, path, date)
        val authorization = "type=${config.svcId}&ver=$tokenVersion&sig=$signature"

        return webClient.post()
            .body(
                BodyInserters.fromObject(body)
            )
            .header("x-ms-version", "2018-12-31")
            .header("x-ms-date", date)
            .header("Authorization", URLEncoder.encode(authorization, urlEncoding))
            .header("Content-Type", "application/json+query")
    }
}
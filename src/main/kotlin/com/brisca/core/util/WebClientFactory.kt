package com.brisca.core.util

import com.brisca.core.config.EndPointConfig
import com.brisca.core.exception.ServiceTimedOutException
import com.brisca.core.exception.ServiceUnavailableException
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.http.HttpStatus
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.TimeUnit


@Component
class WebClientFactory(private val webClientBuilder: WebClient.Builder) {
    val reactorResourceFactory = ReactorResourceFactory()

    fun create(
        endPointConfig: EndPointConfig
    ): WebClient {
        return webClientBuilder
            .baseUrl(endPointConfig.baseUrl)
            .filter { request, next ->
                next.exchange(request)
                    .doOnNext {

                        when (it.statusCode()) {
                            HttpStatus.SERVICE_UNAVAILABLE -> throw ServiceUnavailableException("${endPointConfig.baseUrl.split("/").last()} is unavailable")
                            HttpStatus.GATEWAY_TIMEOUT -> throw ServiceTimedOutException("${endPointConfig.baseUrl.split("/").last()} gateway time out")
                        }
                    }
                    .retry(endPointConfig.retryCount) { it is ReadTimeoutException || (it is ServiceUnavailableException) }
                    .onErrorMap {
                        when (it) {
                            is ReadTimeoutException -> ServiceTimedOutException("${endPointConfig.baseUrl.split("/").last()} timed out")
                            else -> it
                        }
                    }
            }
            .filter(ExchangeFilterFunctions.basicAuthentication(endPointConfig.svcId, endPointConfig.svcPassword))
            .clientConnector(ReactorClientHttpConnector(reactorResourceFactory) { client ->
                client
                    .compress(true)
                    .tcpConfiguration { tcpClient ->
                        tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (endPointConfig.connectTimeoutInSecs).times(1000))
                            .doOnConnect { bootStrap ->
                                bootStrap.handler(ReadTimeoutHandler(
                                (endPointConfig.readTimeoutInSecs).times(1000).toLong(),
                                TimeUnit.MILLISECONDS)
                            )}

                    }

            })
            .build()
    }
}
package com.brisca.core.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


@Component
class WebClientFactory(@Autowired val webClientBuilder: WebClient.Builder) {
//    val reactorResourceFactory = ReactorResourceFactory()

//    fun create(
//        endPointConfig: Any
//    ): WebClient {
//        return webClientBuilder
//            .baseUrl(endPointConfig.baseUrl)
//            .filter { request, next ->
//                next.exchange(request)
//                    .doOnNext {
//                        when (it.statusCode()) {
//                            HttpStatus.SERVICE_UNAVAILABLE -> throw ServiceUnavailableException("${endPointConfig.baseUrl.split("/").last()} is unavailable")
//                            HttpStatus.GATEWAY_TIMEOUT -> throw ServiceTimedOutException("${endPointConfig.baseUrl.split("/").last()} gateway time out")
//                        }
//                    }
//                    .retry(endPointConfig.retryCount) { it is ReadTimeoutException || (it is ServiceUnavailableException) }
//                    .onErrorMap {
//                        when (it) {
//                            is ReadTimeoutException -> ServiceTimedOutException("${endPointConfig.baseUrl.split("/").last()} timed out")
//                            else -> it
//                        }
//                    }
//            }
////            .clientConnector(ReactorClientHttpConnector(reactorResourceFactory) { client ->
////                client
////                    .compress(true)
////                    .tcpConfiguration { tcpClient ->
////                        tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (endPointConfig.connectTimeoutInSecs).times(1000))
////                            .doOnConnect { bootStrap ->
////                                bootStrap.handler(ReadTimeoutHandler(
////                                (endPointConfig.readTimeoutInSecs).times(1000).toLong(),
////                                TimeUnit.MILLISECONDS)
////                            )}
////
////                    }
////
////            })
//            .build()
//    }
}
package com.brisca.core.util

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.springframework.stereotype.Component

@Component
class CircuitBreakerFactory {
    fun <T> get(circuitBreaker: CircuitBreaker): CircuitBreakerOperator<T> = CircuitBreakerOperator.of(circuitBreaker)
}
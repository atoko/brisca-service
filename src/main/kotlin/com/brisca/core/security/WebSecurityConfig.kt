package com.brisca.core.security

import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import java.lang.Compiler.disable
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.anyExchange
import org.springframework.security.authorization.AuthenticatedReactiveAuthorizationManager.authenticated
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val securityContextRepository: SecurityContextRepository? = null

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .exceptionHandling()
                .authenticationEntryPoint { swe, e -> Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED } }
                .accessDeniedHandler { swe, e -> Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN } }
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository!!)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/_briscas**").permitAll()
                .anyExchange().permitAll()
                .and()
                .build()
    }
}
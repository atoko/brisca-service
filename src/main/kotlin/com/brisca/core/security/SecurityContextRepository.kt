package com.brisca.core.security

import reactor.core.publisher.Mono
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.security.core.context.SecurityContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component


@Component
class SecurityContextRepository : ServerSecurityContextRepository {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    override fun save(swe: ServerWebExchange, sc: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val request = swe.request
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val authToken = authHeader.substring(7)
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
            return this.authenticationManager!!.authenticate(auth).map { authentication -> SecurityContextImpl(authentication) }
        } else {
            return Mono.empty()
        }
    }

}
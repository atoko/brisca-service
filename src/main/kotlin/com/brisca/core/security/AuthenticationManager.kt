package com.brisca.core.security

import com.brisca.core.security.model.Role
import com.brisca.core.util.JwtTools
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.ArrayList
import org.springframework.beans.factory.annotation.Autowired


@Component
class AuthenticationManager(
        @Autowired val jwtTools: JwtTools
): ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val authToken = authentication?.credentials.toString()

        var username: String?
        try {
            username = jwtTools.getUsernameFromToken(authToken)
        } catch (e: Exception) {
            username = null
        }

        if (username != null && jwtTools.validateToken(authToken)) {
            val claims = jwtTools.getAllClaimsFromToken(authToken)
            val rolesMap = claims.get("role", List::class.java)
            val roles = ArrayList<Role>()
            for (rolemap in rolesMap) {
                roles.add(Role.valueOf(rolemap as String))
            }
            val auth = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    roles.stream()
                        .map { authority -> SimpleGrantedAuthority(authority.name) }
                        .collect(Collectors.toList())
            )
            return Mono.just(auth)
        } else {
            return Mono.empty()
        }
    }
}
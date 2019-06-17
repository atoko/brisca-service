package com.brisca.core.util

import com.brisca.core.security.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.io.Serializable
import java.time.Instant
import java.util.*

@ConfigurationProperties("brisca.jwt")
@Component
class JwtTools(

) {
    lateinit var secret: String
    lateinit var expiration: String

    fun getParser() : JwtParser {
        if (parser == null) {
            parser = Jwts.parser().setSigningKey(
                Base64.getEncoder().encodeToString(secret.toByteArray())
            )
        }

        return parser!!
    }

    fun getAllClaimsFromToken(token: String): Claims {
        return getParser().parseClaimsJws(token).body
    }

    fun getUsernameFromToken(token: String): String {
        return getAllClaimsFromToken(token)["iss"].toString()
    }

    fun getExpirationDateFromToken(token: String): Date {
        val expires: Number = getAllClaimsFromToken(token)["exp"] as Number
        return Date.from(Instant.ofEpochSecond(expires.toLong()))
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: User): String {
        val claims = HashMap<String, Any>()
        claims["role"] = user.roles
        return doGenerateToken(claims, user.id)
    }

    private fun doGenerateToken(claims: Map<String, Any>, username: String): String {
        val expirationTimeLong = java.lang.Long.parseLong(expiration!!) //in second

        val createdDate = Date()
        val expirationDate = Date(createdDate.getTime() + expirationTimeLong * 1000)
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret!!.toByteArray()))
                .compact()
    }

    fun validateToken(token: String): Boolean {
        return (!isTokenExpired(token))
    }

    companion object {
        var parser: JwtParser? = null
        private const val serialVersionUID = 1L
    }

}
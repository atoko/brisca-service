package com.brisca.core.util

import com.brisca.core.config.CosmosConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class CosmosSignatureCache(
    @Autowired val config: CosmosConfig
) {
    private val decodedKey = Base64.getDecoder().decode(config.svcPassword)

    private fun encrypt(payload: String): ByteArray {
        val secret = decodedKey
        val message = payload.toByteArray()

        val sha256_HMAC = Mac.getInstance("HmacSHA256")
        val secret_key = SecretKeySpec(secret, "HmacSHA256")
        sha256_HMAC.init(secret_key)

        return sha256_HMAC.doFinal(message)
    }


    private fun encryptedSignature(verb: String, type: String, path: String, date: String): ByteArray {
        val root = generateSignature(verb, type, path)
        return encrypt(datedSignature(root, date))
    }

    public fun retrieve(verb: String, type: String, path: String, date: String): String {
        val encrypted = encryptedSignature(verb, type, path, date)
        return encoder.encodeToString(encrypted)
    }

    companion object {
        private val encoder = Base64.getEncoder()

        private fun generateSignature(verb: String,  type: String, path: String): String {
            //verb is lowercase
            //resource is lowercase
            return "$verb\n$type\n$path"
        }

        private fun datedSignature(root: String, date: String): String {
            //date is lowercase
            return "$root\n$date\n \n"
        }
    }
}
package com.brisca.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity

@SpringBootApplication(scanBasePackages = ["com.brisca.core.*"])
class BriscaCoreApplication

    fun main(args: Array<String>) {
        runApplication<BriscaCoreApplication>(*args)
    }



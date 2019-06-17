package com.brisca.core

import com.microsoft.azure.spring.data.cosmosdb.repository.config.EnableDocumentDbRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.brisca.core.*"])
@EnableDocumentDbRepositories
class BriscaCoreApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<BriscaCoreApplication>(*args)
        }
    }

}



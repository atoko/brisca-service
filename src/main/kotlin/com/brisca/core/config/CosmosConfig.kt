package com.brisca.core.config

import com.microsoft.azure.spring.data.cosmosdb.config.AbstractDocumentDbConfiguration
import com.microsoft.azure.spring.data.cosmosdb.config.DocumentDBConfig
import com.microsoft.azure.spring.data.cosmosdb.repository.config.EnableDocumentDbRepositories
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
@EnableDocumentDbRepositories
class CosmosConfig : AbstractDocumentDbConfiguration() {

    @Value("\${azure.cosmosdb.uri}")
    private val uri: String? = null

    @Value("\${azure.cosmosdb.key}")
    private val key: String? = null

    @Value("\${azure.cosmosdb.database}")
    private val dbName: String? = null

    override fun getConfig(): DocumentDBConfig {
        return DocumentDBConfig.builder(uri, key, dbName).build()
    }
}
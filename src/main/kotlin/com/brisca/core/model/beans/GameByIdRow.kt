package com.brisca.core.model.beans

import com.brisca.core.model.domain.GameData
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.PartitionKey
import org.springframework.data.annotation.Id
import java.util.Date

@Document(collection = "briscasById")
data class GameByIdRow(var createdAt: Date = Date()) {
    @PartitionKey
    @Id
    var gameId: String? = null
    var data: GameData? = null
    var apiVersion: Int = 1

    constructor(data: GameData): this() {
        this.data = data
    }
}


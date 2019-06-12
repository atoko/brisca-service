package com.brisca.core.model.beans

import com.brisca.core.model.domain.GameData
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.Date

interface IGameRow {
    fun id(): Long?
    fun data(): String
    fun createdAt(): Date
    fun modified(): Date?
    fun version(): String
}

data class GameRow(var rawData: String = "", var createdAt: Date = Date()) {
    var id: String? = null
    var modified: Date? = null

    @JsonIgnore
    var data: GameData? = null

    companion object {
        private val objectMapper: ObjectMapper = ObjectMapper()
    }

    constructor(data: GameData): this() {
        this.data = data
        this.rawData = objectMapper.writeValueAsString(data)
    }

    constructor(row: IGameRow): this(row.data(), row.createdAt()){
        this.id = row.id().toString()
        this.modified = row.modified()
        this.data = objectMapper.readValue(row.data())
    }
}


package com.brisca.core.model.domain

data class PlayerData(
        val id: String,
        val next: String?,
        val hand: MutableCollection<Int> = mutableSetOf(),
        private var points: Int = 0
)
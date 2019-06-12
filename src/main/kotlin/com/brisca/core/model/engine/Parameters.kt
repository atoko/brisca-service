package com.brisca.core.model.engine

interface Parameters {
    val totalCards: Int
    val handSize: Int
    val pointMapping: Map<Int,Int>
}

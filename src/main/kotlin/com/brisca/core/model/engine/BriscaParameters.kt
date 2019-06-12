package com.brisca.core.model.engine

class BriscaParameters private constructor() : Parameters {
    override val totalCards: Int = 40
    override val handSize: Int = 3
    override val pointMapping: Map<Int, Int> = hashMapOf(
            0 to 11,
            2 to 10,
            7 to 2,
            8 to 3,
            9 to 4
    )

    companion object {
        val instance: BriscaParameters = BriscaParameters()
    }
}
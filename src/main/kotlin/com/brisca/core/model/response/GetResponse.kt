package com.brisca.core.model.response

data class GetResponse(val data: List<GameResponse> = emptyList()) {
    constructor() : this(emptyList())
}

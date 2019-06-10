package com.brisca.core.security.model

import com.brisca.core.security.model.Role

data class User(
        val id: String,
        val session: String,
        val roles: List<Role> = listOf()
)
package com.mrozenblum.poatp.domain

data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val points: Long
)

data class UserResponse(
    val userId: Long
)
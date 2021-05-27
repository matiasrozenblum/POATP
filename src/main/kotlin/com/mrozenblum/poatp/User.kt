package com.mrozenblum.poatp

data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val points: Long
)

data class UserResponse(
    val transactionId: Long
)

fun User.asResponse() = UserResponse(id!!)
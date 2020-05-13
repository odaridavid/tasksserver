package com.github.odaridavid.models

data class ToDo(
    val id: Int,
    val userId: Int,
    val todo: String,
    val done: Boolean
)
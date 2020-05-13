package com.github.odaridavid.models

data class Task(
    val id: Int,
    val userId: Int,
    val task: String,
    val isDone: Boolean
)
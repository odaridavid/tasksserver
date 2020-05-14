package com.github.odaridavid.repositories

import com.github.odaridavid.models.Task

interface ITaskRepository {

    suspend fun postTask(userId: Int, task: String, isDone: Boolean = false): Task?

    suspend fun getAllTasks(userId: Int): List<Task>

    suspend fun getTask(userId: Int, id: Int): Task?

    suspend fun filterTasksByCompletion(userId: Int, isDone: Boolean): List<Task>

    suspend fun deleteTask(userId: Int, id: Int): Int

    suspend fun deleteAllTasks(userId: Int): Int
}
package com.github.odaridavid.repositories

import com.github.odaridavid.db.Tasks
import com.github.odaridavid.models.Task
import com.github.odaridavid.utils.executeDbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class TaskRepository : ITaskRepository {

    override suspend fun postTask(userId: Int, task: String, isDone: Boolean): Task? {
        var statement: InsertStatement<Number>? = null
        executeDbQuery {
            statement = Tasks.insert { column ->
                column[Tasks.userId] = userId
                column[Tasks.task] = task
                column[Tasks.isDone] = isDone
            }
        }
        return rowToToDo(statement?.resultedValues?.get(0))
    }

    override suspend fun getAllTasks(userId: Int): List<Task> {
        return executeDbQuery {
            Tasks.select { Tasks.userId eq userId }
                .mapNotNull { row -> rowToToDo(row) }
        }
    }

    override suspend fun getTask(userId: Int, id: Int): Task? {
        return executeDbQuery {
            Tasks.select { (Tasks.id eq id) and (Tasks.userId eq userId) }
                .map { row -> rowToToDo(row) }
                .singleOrNull()
        }
    }

    override suspend fun filterTasksByCompletion(userId: Int, isDone: Boolean): List<Task> {
        return executeDbQuery {
            Tasks.select { (Tasks.isDone eq isDone) and (Tasks.userId eq userId) }
                .mapNotNull { row -> rowToToDo(row) }
        }
    }

    override suspend fun deleteTask(userId: Int, id: Int) {
        executeDbQuery {
            Tasks.deleteWhere { (Tasks.userId eq userId) and (Tasks.id eq id) }
        }
    }

    override suspend fun deleteAllTasks(userId: Int) {
        executeDbQuery {
            Tasks.deleteWhere { Tasks.userId eq userId }
        }
    }

    private fun rowToToDo(row: ResultRow?): Task? {
        return row?.run {
            Task(
                id = row[Tasks.id],
                userId = row[Tasks.userId],
                task = row[Tasks.task],
                isDone = row[Tasks.isDone]
            )
        }
    }

}
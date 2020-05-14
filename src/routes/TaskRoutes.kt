package com.github.odaridavid.routes

import com.github.odaridavid.Constants.Keys.IS_DONE
import com.github.odaridavid.Constants.Keys.TASK
import com.github.odaridavid.models.User
import com.github.odaridavid.repositories.ITaskRepository
import com.github.odaridavid.repositories.IUserRepository
import com.github.odaridavid.sessions.TaskSession
import com.github.odaridavid.utils.*
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.delete
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.util.pipeline.PipelineContext
import com.github.odaridavid.Constants.Routes.FILTER_TASKS as FILTER_TASKS_ROUTE
import com.github.odaridavid.Constants.Routes.TASK as TASK_ROUTE
import com.github.odaridavid.Constants.Routes.TASKS as TASKS_ROUTE


@KtorExperimentalLocationsAPI
@Location(TASKS_ROUTE)
class TasksRoute

@KtorExperimentalLocationsAPI
@Location(TASK_ROUTE)
data class TaskRoute(val id: Int)

@KtorExperimentalLocationsAPI
@Location(FILTER_TASKS_ROUTE)
data class FilterTasksRoute(val isDone: Boolean)


@KtorExperimentalLocationsAPI
fun Route.tasks(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    authenticate("jwt") {

        postTask(taskRepository, userRepository)

        getAllTasks(taskRepository, userRepository)

        deleteAllTasks(taskRepository, userRepository)

        filterTasksByCompletion(taskRepository, userRepository)

        getTask(taskRepository, userRepository)

        deleteTask(taskRepository, userRepository)

    }

}

@KtorExperimentalLocationsAPI
private fun Route.getAllTasks(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    get<TasksRoute> {

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            respondWithBadRequest("No Active User Session")
            return@get
        }
        performRequest("Get All User Tasks") {

            val tasks = taskRepository.getAllTasks(user.userId)

            respondWithOk(tasks)
        }
    }

}

@KtorExperimentalLocationsAPI
private fun Route.postTask(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    post<TasksRoute> {

        val taskParams = call.receive<Parameters>()

        val task = taskParams[TASK] ?: return@post respondWithBadRequest("No Task Defined")
        val isDone = taskParams[IS_DONE] ?: "false"

        with(Validator) {
            if (!isValidTask(task)) return@post respondWithBadRequest("Task Can't Be Blank")
        }

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            respondWithBadRequest("No Active User Session")
            return@post
        }

        performRequest("Post Task") {

            val createdTask = taskRepository.postTask(user.userId, task, isDone.toBoolean())

            createdTask?.run { respondWithCreated(createdTask) } ?: respondWithBadRequest("Error Creating Task")
        }

    }

}

@KtorExperimentalLocationsAPI
private fun Route.deleteAllTasks(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    delete<TasksRoute> {

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            respondWithBadRequest("No Active User Session")
            return@delete
        }

        performRequest("Delete All User Tasks") {
            val result = taskRepository.deleteAllTasks(user.userId)
            if (result != 0)
                respondWithOk("Deleted All Tasks Successfully")
            else
                respondWithNotFound("Oops! Something Went Wrong")
        }

    }

}

@KtorExperimentalLocationsAPI
private fun Route.deleteTask(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    delete<TaskRoute> { specificTaskRoute ->

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            respondWithBadRequest("No Active User Session")
            return@delete
        }

        performRequest("Delete Task of Id:${specificTaskRoute.id}") {

            val result = taskRepository.deleteTask(user.userId, specificTaskRoute.id)

            if (result != 0)
                respondWithOk("Task Deleted Successfully")
            else
                respondWithNotFound("Oops! Something Went Wrong")
        }

    }
}

@KtorExperimentalLocationsAPI
private fun Route.getTask(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    get<TaskRoute> { specificTaskRoute ->

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            respondWithBadRequest("No Active User Session")
            return@get
        }

        performRequest("Get Specific Task of Id:${specificTaskRoute.id}") {

            val task = taskRepository.getTask(user.userId, specificTaskRoute.id)

            task?.run { respondWithOk(task) } ?: respondWithNotFound("Task Doesn't exist")
        }

    }

}

@KtorExperimentalLocationsAPI
private fun Route.filterTasksByCompletion(taskRepository: ITaskRepository, userRepository: IUserRepository) {
    get<FilterTasksRoute> { filterTasksRoute ->

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            respondWithBadRequest("No Active User Session")
            return@get
        }
        performRequest("Filter Tasks By Completion") {

            val tasks = taskRepository.filterTasksByCompletion(user.userId, filterTasksRoute.isDone)

            respondWithOk(tasks)
        }

    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getUserFromCurrentSession(
    userRepository: IUserRepository
): User? {
    val currentSession = call.sessions.get<TaskSession>()
    return currentSession?.run { userRepository.findUserById(userId) }
}

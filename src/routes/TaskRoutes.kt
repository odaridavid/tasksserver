package com.github.odaridavid.routes

import com.github.odaridavid.Constants.Keys.IS_DONE
import com.github.odaridavid.Constants.Keys.TASK
import com.github.odaridavid.Constants.Routes.TASKS
import com.github.odaridavid.models.User
import com.github.odaridavid.repositories.ITaskRepository
import com.github.odaridavid.repositories.IUserRepository
import com.github.odaridavid.sessions.TaskSession
import com.github.odaridavid.utils.performRequest
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.routing.Route
import io.ktor.locations.get
import io.ktor.locations.delete
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.util.pipeline.PipelineContext


@KtorExperimentalLocationsAPI
@Location(TASKS)
class TasksRoute

@KtorExperimentalLocationsAPI
fun Route.tasks(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    authenticate("jwt") {

        postTask(taskRepository, userRepository)

        getAllTasks(taskRepository, userRepository)

        deleteAllTasks(taskRepository, userRepository)

        filterTasksByCompletion(taskRepository)

        //        getSpecificTask(taskRepository)

        //        deleteSpecificTask(taskRepository)

    }

}

@KtorExperimentalLocationsAPI
private fun Route.getAllTasks(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    get<TasksRoute> {

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "No Active User Session")
            return@get
        }
        performRequest("Get All User Tasks") {
            val tasks = taskRepository.getAllTasks(user.userId)
            call.respond(HttpStatusCode.OK, tasks)
        }
    }

}

@KtorExperimentalLocationsAPI
private fun Route.postTask(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    post<TasksRoute> {

        val taskParams = call.receive<Parameters>()

        val task = taskParams[TASK] ?: return@post call.respond(HttpStatusCode.BadRequest, "No Task Defined")
        val isDone = taskParams[IS_DONE] ?: "false"

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "No Active User Session")
            return@post
        }

        performRequest("Post Task") {
            val createdTask = taskRepository.postTask(user.userId, task, isDone.toBoolean())
            createdTask?.run { call.respond(HttpStatusCode.OK, createdTask) }
        }

    }

}

@KtorExperimentalLocationsAPI
private fun Route.deleteAllTasks(taskRepository: ITaskRepository, userRepository: IUserRepository) {

    delete<TasksRoute> {

        val user = getUserFromCurrentSession(userRepository)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "No Active User Session")
            return@delete
        }

        performRequest("Delete All User Tasks") {
            taskRepository.deleteAllTasks(user.userId)
            call.respond(HttpStatusCode.NoContent, "Deleted all tasks Successfully")
        }

    }

}


@KtorExperimentalLocationsAPI
private fun Route.filterTasksByCompletion(taskRepository: ITaskRepository) {
    //TODO Filter tasks
}

private suspend fun PipelineContext<Unit, ApplicationCall>.getUserFromCurrentSession(
    userRepository: IUserRepository
): User? {
    val currentSession = call.sessions.get<TaskSession>()
    return currentSession?.run { userRepository.findUserById(userId) }
}

//@KtorExperimentalLocationsAPI
//private fun Route.deleteSpecificTask(taskRepository: ITaskRepository) {
//    delete<SpecificTaskRoute> {
//
//    }
//}

//@KtorExperimentalLocationsAPI
//private fun Route.getSpecificTask(taskRepository: ITaskRepository) {
//    get<SpecificTaskRoute> {
//
//    }
//}

//@KtorExperimentalLocationsAPI
//@Location(TASKS)
//class SpecificTaskRoute
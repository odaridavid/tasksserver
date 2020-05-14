package com.github.odaridavid

object Constants {

    const val API_VERSION = "/v1"

    object Routes {
        //Users
        private const val USERS = "$API_VERSION/users"
        const val USER_LOGIN = "$USERS/login"
        const val USER_SIGN_UP = "$USERS/sign-up"

        //Todos
        const val TASKS = "$API_VERSION/tasks"
        const val TASK = "$TASKS/{id}"
        const val FILTER_TASKS = "$TASKS/filter"

    }

    object Keys {
        const val PASSWORD = "password"
        const val DISPLAY_NAME = "displayName"
        const val EMAIL = "email"
        const val TASK = "task"
        const val IS_DONE = "is_done"
    }
}
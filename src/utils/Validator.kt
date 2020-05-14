package com.github.odaridavid.utils

import java.util.regex.Pattern

object Validator {

    fun isValidDisplayName(displayName: String): Boolean {
        val trimmedTask = displayName.trim()
        return trimmedTask.isNotBlank()
    }

    fun isValidPassword(password: String): Boolean {
        val passRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!\\-_?&])(?=\\S+$).{8,}"
        val pattern = Pattern.compile(passRegex)
        return pattern.matcher(password).matches()
    }

    /**
     * Basic email validation making sure @ isn't the only character provided
     */
    fun isValidEmail(email: String): Boolean {
        return email.contains('@') && email.indexOf('@') != 0 && email.indexOf('@') != email.length - 1
    }

    fun isValidTask(task: String): Boolean {
        val trimmedTask = task.trim()
        return trimmedTask.isNotBlank()
    }

}
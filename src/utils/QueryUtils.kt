package com.github.odaridavid.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun <T> executeDbQuery(block: () -> T): T {
    return withContext(Dispatchers.IO) {
        transaction { block() }
    }
}
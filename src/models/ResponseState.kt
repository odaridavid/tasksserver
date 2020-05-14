package com.github.odaridavid.models


sealed class ResponseState

data class ResponseError(val message: String) : ResponseState()

data class ResponseSuccess<T>(val data: T) : ResponseState()
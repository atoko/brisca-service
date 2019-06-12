package com.brisca.core.exception

import org.springframework.http.HttpStatus

open class CodedRuntimeException(val code: String, val reason: String, val httpStatus: HttpStatus) : RuntimeException()
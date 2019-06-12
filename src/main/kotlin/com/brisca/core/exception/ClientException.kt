package com.brisca.core.exception

import org.springframework.http.HttpStatus

class ServiceUnavailableException(reason: String) : CodedRuntimeException("INTERNAL_SERVER_ERROR", reason, HttpStatus.INTERNAL_SERVER_ERROR)

class ServiceTimedOutException(reason: String) : CodedRuntimeException("INTERNAL_SERVER_ERROR", reason, HttpStatus.INTERNAL_SERVER_ERROR)

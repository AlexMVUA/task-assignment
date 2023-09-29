package com.mykha.task.repository

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ItemNotFoundException(message: String? = "Item not found"): RuntimeException(message)
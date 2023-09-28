package com.mykha.task.tool

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidGenerator {
    fun generateUuid(): UUID = UUID.randomUUID()
}

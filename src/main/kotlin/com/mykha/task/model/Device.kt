package com.mykha.task.model

data class Device(
    val serialNumber: String,
    var uuid: String? = null,
    val phoneNumber: String,
    val model: String?,
    val userId: String? = null
)


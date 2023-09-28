package com.mykha.task.model

data class Device(
    val serialNumber: String,
    var uuid: String?,
    val phoneNumber: String,
    val model: String?
)


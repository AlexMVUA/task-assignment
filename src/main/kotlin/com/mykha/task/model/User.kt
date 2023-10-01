package com.mykha.task.model

import java.util.Date

data class User(
    var id: String?,
    val firstName: String,
    val lastName: String,
    val address: String,
    val birthday: Date,
    var devices: List<Device> = emptyList()
)

data class DeviceAssignment(
    val deviceUuid: String,
    val userId: String
)

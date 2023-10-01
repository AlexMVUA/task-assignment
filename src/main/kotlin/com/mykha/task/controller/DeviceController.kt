package com.mykha.task.controller

import com.mykha.task.model.Device
import com.mykha.task.repository.DeviceRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/device")
class DeviceController(
    private val deviceRepository: DeviceRepository
) {

    @PostMapping
    fun createDevice(@RequestBody device: Device): ResponseEntity<String> {
        val deviceUuid = deviceRepository.createDevice(device)
        return ResponseEntity<String>(deviceUuid, HttpStatus.CREATED)
    }

    @GetMapping("/{uuid}")
    fun getDeviceByUuid(@PathVariable uuid: String): ResponseEntity<Device> {
        val device = deviceRepository.getDeviceByUuid(uuid)
        return ResponseEntity<Device>(device, HttpStatus.OK)
    }
}

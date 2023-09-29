package com.mykha.task.controller

import com.mykha.task.model.Device
import com.mykha.task.repository.DeviceRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus


class DeviceControllerTest {

    private val defaultUuid: String = "d3cd6562-feca-4e48-b752-39078b0d977a"
    private val defaultDevice = mockk<Device>()
    private val deviceRepository = mockk<DeviceRepository>()
    private val deviceController = DeviceController(deviceRepository)

    @BeforeEach
    fun setUp() {
        every { deviceRepository.getDeviceByUuid(any()) } returns defaultDevice
        every { deviceRepository.createDevice(defaultDevice) } returns defaultUuid
    }

    @Test
    fun shouldCreateDevice() {
        val resultUuid = deviceController.createDevice(defaultDevice)
        assertThat(resultUuid.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(resultUuid.body).isEqualTo(defaultUuid)
    }

    @Test
    fun shouldReturnDeviceByUuid() {
        val response = deviceController.getDeviceByUuid(defaultUuid)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(defaultDevice)
    }
}
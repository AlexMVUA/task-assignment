package com.mykha.task.controller

import com.mykha.task.model.DeviceAssignment
import com.mykha.task.model.User
import com.mykha.task.repository.Paged
import com.mykha.task.repository.PagedRequest
import com.mykha.task.repository.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus

class UserControllerTest {

    private val limit = 10L
    private val offset = 0L
    private val userId = "user_id"
    private val deviceUuid = "d3cd6562-feca-4e48-b752-39078b0d977a"
    private val user = mockk<User>()
    private val pagedResults = mockk<Paged<User>>()
    private val userRepository = mockk<UserRepository>()
    private val userController = UserController(userRepository)

    @BeforeEach
    fun setUp() {
        every {
            userRepository.assignDeviceToUser(any<DeviceAssignment>())
        } just runs
        every { userRepository.createUser(user) } returns userId
    }

    @Test
    fun shouldCreateUser() {
        val response = userController.createUser(user)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        Assertions.assertThat(response.body).isEqualTo(userId)
    }

    @Test
    fun shouldAssignDevice() {
        val response = userController.assignDevice(userId, deviceUuid)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        Assertions.assertThat(response.body).isNull()
    }

    @Test
    fun shouldReturnPagedUsers() {
        val pageRequest = PagedRequest(limit = limit, offset = offset)
        every {
            userRepository.getAllUsers(pageRequest)
        } returns pagedResults

        val response = userController.getAllUsers(limit, offset)

        verify {
            userRepository.getAllUsers(pageRequest)
        }
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(response.body).isEqualTo(pagedResults)

    }
}

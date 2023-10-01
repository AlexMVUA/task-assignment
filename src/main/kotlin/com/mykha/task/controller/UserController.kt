package com.mykha.task.controller

import com.mykha.task.model.DeviceAssignment
import com.mykha.task.model.User
import com.mykha.task.repository.Paged
import com.mykha.task.repository.PagedRequest
import com.mykha.task.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userRepository: UserRepository
) {
    companion object {
        const val DEFAULT_MAX_RESULTS = 5L
        const val DEFAULT_OFFSET = 0L
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<String> {
        val userId = userRepository.createUser(user)
        return ResponseEntity<String>(userId, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllUsers(
        @RequestParam limit: Long = DEFAULT_MAX_RESULTS,
        @RequestParam offset: Long = DEFAULT_OFFSET
    ): ResponseEntity<Paged<User>> {
        val pagedRequest = PagedRequest(limit = limit, offset = offset)
        val users = userRepository.getAllUsers(pagedRequest)
        return ResponseEntity<Paged<User>>(users, HttpStatus.OK)
    }

    @PostMapping("{userId}/device/{deviceUuid}")
    fun assignDevice(
        @PathVariable userId: String,
        @PathVariable deviceUuid: String
    ): ResponseEntity<Void> {
        val deviceAssignment = DeviceAssignment(
            deviceUuid = deviceUuid,
            userId = userId
        )
        userRepository.assignDeviceToUser(deviceAssignment)
        return ResponseEntity<Void>(HttpStatus.CREATED)
    }
}

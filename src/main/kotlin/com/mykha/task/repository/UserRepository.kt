package com.mykha.task.repository

import com.mykha.task.model.Device
import com.mykha.task.model.DeviceAssignment
import com.mykha.task.model.User
import com.mykha.task.tool.UuidGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class UserRepository(
    val jdbcTemplate: JdbcTemplate,
    val uuidGenerator: UuidGenerator,
    val deviceRepository: DeviceRepository
) {
    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(UserRepository::class.java)
    }

    fun createUser(user: User): String {
        user.id = uuidGenerator.generateUuid().toString()
        logger.debug("debug {}", user)
        jdbcTemplate.update(
            "insert into users (id, firstName, lastName, address, birthday) values (?,?,?,?,?)",
            user.id, user.firstName, user.lastName, user.address, user.birthday
        )
        return user.id!!
    }

    fun getAllUsers(pageRequest: PagedRequest): Paged<User> {
        val users = getPagedUsers(pageRequest)
        populateDevices(users)

        logger.debug("debug {}", users)

        return Paged(
            values = users,
            maxResults = pageRequest.limit,
            startAt = pageRequest.offset,
            total = getUserCount()
        )
    }

    private fun getPagedUsers(pageRequest: PagedRequest): MutableList<User> =
        jdbcTemplate.query(
            "select id, firstName, lastName, address, birthday from users limit ? offset ?",
            UserMapper(),
            pageRequest.limit,
            pageRequest.offset
        )

    private fun populateDevices(users: List<User>) {
        for (user in users) {
            val devices: List<Device> = getUserDevices(user)
            user.devices = devices
        }
    }

    fun assignDeviceToUser(deviceAssignment: DeviceAssignment) {
        validateUserExist(deviceAssignment.userId)

        deviceRepository.assignDevice(deviceAssignment)
    }

    private fun getUserCount(): Long = jdbcTemplate.queryForObject<Long>("select count(*) from users")

    private fun getUserDevices(user: User): List<Device> = deviceRepository.getDevicesByUserId(user.id!!)

    private fun validateUserExist(userId: String) {
        jdbcTemplate.query(
            "select id from users where id=?", { resultSet, _ ->
                resultSet.getString("id")
            },
            userId
        ).ifEmpty {
            throw ItemNotFoundException("User with id: $userId doesn't exist")
        }
    }
}

class UserMapper : RowMapper<User> {
    override fun mapRow(resultSet: ResultSet, rowNum: Int): User =
        User(
            id = resultSet.getString("id"),
            firstName = resultSet.getString("firstName"),
            lastName = resultSet.getString("lastName"),
            address = resultSet.getString("address"),
            birthday = resultSet.getDate("birthday")
        )
}

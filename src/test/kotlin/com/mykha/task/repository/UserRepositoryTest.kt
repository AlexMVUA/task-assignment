package com.mykha.task.repository

import com.mykha.task.model.Device
import com.mykha.task.model.DeviceAssignment
import com.mykha.task.model.User
import com.mykha.task.tool.UuidGenerator
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForObject
import java.sql.ResultSet
import java.util.Date
import java.util.UUID

class UserRepositoryTest {

    private val createUserQuery = "insert into users (id, firstName, lastName, address, birthday) values (?,?,?,?,?)"
    private val getAllUsersQuery = "select id, firstName, lastName, address, birthday from users limit ? offset ?"
    private val getUserByIdQuery = "select id from users where id=?"
    private val getUserCountQuery = "select count(*) from users"
    private val sqlDateFirst = java.sql.Date(System.currentTimeMillis())
    private val dateFirst = Date(sqlDateFirst.time)
    private val createdUserUuid = UUID.fromString("d3cd6562-feca-4e48-b752-39078b0d977a")
    private val createdUserUuidValue = "d3cd6562-feca-4e48-b752-39078b0d977a"
    private val secondUserId = "user_id_second"
    private val userToBeCreated = User(
        id = null,
        firstName = "name",
        lastName = "last_name",
        address = "some_address",
        birthday = dateFirst
    )
    private val pagedRequest = PagedRequest(limit = 10, offset = 0)
    private val existingDeviceUuidValue = "99d2f603-9d7c-4dd4-84f4-ceccee8d3696"
    private val deviceFirst = Device(
        uuid = existingDeviceUuidValue,
        model = "first_model",
        serialNumber = "serial",
        phoneNumber = "1234",
        userId = createdUserUuidValue
    )
    private val deviceSecond = deviceFirst.copy(model = "second_model", userId = secondUserId)
    private val deviceThird = deviceSecond.copy(model = "third_model")
    private val jdbcTemplate = mockk<JdbcTemplate>()
    private val uuidGenerator = mockk<UuidGenerator>()
    private val deviceRepository = mockk<DeviceRepository>()

    private val userRepository = UserRepository(jdbcTemplate, uuidGenerator, deviceRepository)

    @BeforeEach
    fun setUp() {
        every { uuidGenerator.generateUuid() } returns createdUserUuid
        every { deviceRepository.assignDevice(any<DeviceAssignment>()) } just runs
    }

    @Test
    fun shouldCreateUser() {
        mockJdbcForUserCreation()
        val uuidValue = userRepository.createUser(userToBeCreated)

        assertThat(uuidValue).isEqualTo(createdUserUuidValue)

        verify {
            jdbcTemplate.update(
                createUserQuery,
                createdUserUuidValue,
                userToBeCreated.firstName,
                userToBeCreated.lastName,
                userToBeCreated.address,
                userToBeCreated.birthday
            )
        }
    }

    private fun mockJdbcForUserCreation() {
        every {
            jdbcTemplate.update(
                createUserQuery,
                any<String>(),
                any<String>(),
                any<String>(),
                any<String>(),
                any<String>(),
            )
        } returns 1
    }

    @Test
    fun assignDeviceToUserShouldThrowExceptionWhenUserNotFound() {
        mockEmptyResultForGetUserById()
        val deviceAssignment = DeviceAssignment(deviceUuid = existingDeviceUuidValue, userId = createdUserUuidValue)

        val expectedException: Exception = assertThrows {
            userRepository.assignDeviceToUser(deviceAssignment)
        }
        assertThat(expectedException).isInstanceOf(ItemNotFoundException::class.java)
        assertThat(expectedException.message)
            .isEqualTo("User with id: $createdUserUuidValue doesn't exist")
    }

    private fun mockEmptyResultForGetUserById() {
        every {
            jdbcTemplate.query(
                getUserByIdQuery,
                any<RowMapper<String>>(),
                any<String>()
            )
        } returns emptyList()
    }

    @Test
    fun shouldAssignDeviceToUser() {
        mockUserResultForGetUserById()
        val deviceAssignment = DeviceAssignment(deviceUuid = existingDeviceUuidValue, userId = createdUserUuidValue)

        userRepository.assignDeviceToUser(deviceAssignment)

        verify {
            deviceRepository.assignDevice(deviceAssignment)
        }
    }

    private fun mockUserResultForGetUserById() {
        every {
            jdbcTemplate.query(
                getUserByIdQuery,
                any<RowMapper<String>>(),
                createdUserUuidValue
            )
        } returns listOf(createdUserUuidValue)
    }

    @Test
    fun shouldMapUserViaUserMapper() {
        val user = userToBeCreated.copy(id = createdUserUuidValue)

        val resultSet = mockk<ResultSet> {
            every {
                getString("id")
            } returns user.id
            every {
                getString("firstName")
            } returns user.firstName
            every {
                getString("lastName")
            } returns user.lastName
            every {
                getString("address")
            } returns user.address
            every {
                getDate("birthday")
            } returns sqlDateFirst
        }

        assertThat(UserMapper().mapRow(resultSet, ArgumentMatchers.anyInt())).isEqualTo(user)
    }

    @Test
    fun shouldReturnUsers() {
        val userFirst = userToBeCreated.copy(id = createdUserUuidValue)
        val userSecond = userToBeCreated.copy(id = secondUserId)
        mockUsersResultByPagedRequest(userFirst, userSecond)
        every {
            deviceRepository.getDevicesByUserId(userFirst.id!!)
        } returns listOf(deviceFirst)
        every {
            deviceRepository.getDevicesByUserId(userSecond.id!!)
        } returns listOf(deviceSecond, deviceThird)

        every {
            jdbcTemplate.queryForObject<Long>(getUserCountQuery)
        } returns 3L

        val actualPagedResult = userRepository.getAllUsers(pagedRequest)

        assertUsersPagedResult(actualPagedResult, pagedRequest, listOf(userFirst, userSecond), 3L)
    }

    private fun mockUsersResultByPagedRequest(userFirst: User, userSecond: User) {
        every {
            jdbcTemplate.query(
                getAllUsersQuery,
                any<UserMapper>(),
                pagedRequest.limit,
                pagedRequest.offset
            )
        } returns listOf(userFirst, userSecond)
    }

    private fun assertUsersPagedResult(
        pagedResult: Paged<User>,
        pagedRequest: PagedRequest,
        userExpected: List<User>,
        totalUsersCount: Long
    ) {
        assertThat(pagedResult.values).containsExactlyElementsOf(userExpected)
        assertThat(pagedResult.startAt).isEqualTo(pagedRequest.offset)
        assertThat(pagedResult.maxResults).isEqualTo(pagedRequest.limit)
        assertThat(pagedResult.total).isEqualTo(totalUsersCount)
    }
}

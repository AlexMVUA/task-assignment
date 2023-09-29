package com.mykha.task.repository

import com.mykha.task.model.Device
import com.mykha.task.tool.UuidGenerator
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.UUID

class DeviceRepositoryTest {

    private val insertQuery = "insert into devices (uuid, serialNumber, phoneNumber, model) values ( ?, ? ,?,?)"
    private val getDeviceByUuidQuery = "select uuid, serialNumber, phoneNumber, model from devices where uuid=?"
    private val uuid: UUID = UUID.fromString("d3cd6562-feca-4e48-b752-39078b0d977a")

    private val jdbcTemplate = mockk<JdbcTemplate>()
    private val uuidGenerator = mockk<UuidGenerator>()
    private val deviceRepository = DeviceRepository(jdbcTemplate, uuidGenerator)
    private lateinit var deviceToBeCreated: Device

    @BeforeEach
    fun setUp() {
        deviceToBeCreated = Device(
            serialNumber = "ABC-25",
            phoneNumber = "123456",
            model = "S4"
        )
        every { uuidGenerator.generateUuid() } returns uuid
        every {
            jdbcTemplate.update(
                insertQuery,
                uuid.toString(),
                deviceToBeCreated.serialNumber,
                deviceToBeCreated.phoneNumber,
                deviceToBeCreated.model
            )
        } returns 1
    }

    @Test
    fun shouldAddUuidForCreatedDevice() {
        deviceRepository.createDevice(deviceToBeCreated)
        assertThat(deviceToBeCreated.uuid).isEqualTo(uuid.toString())
    }

    @Test
    fun shouldReturnUuidValueForCreatedDevice() {
        val uuidValue = deviceRepository.createDevice(deviceToBeCreated)
        assertThat(uuidValue).isEqualTo(uuid.toString())
    }

    @Test
    fun shouldReturnExistingDeviceByUuid() {
        every {
            jdbcTemplate.query(
                getDeviceByUuidQuery,
                any<RowMapper<Device>>(),
                uuid.toString()
            )
        } returns mutableListOf(deviceToBeCreated)

        val deviceByUuid = deviceRepository.getDeviceByUuid(uuid.toString())
        assertThat(deviceByUuid).isEqualTo(deviceByUuid)
    }

    @Test
    fun shouldThrowExceptionWhenNoResultsReturned() {
        every {
            jdbcTemplate.query(
                getDeviceByUuidQuery,
                any<RowMapper<Device>>(),
                uuid.toString()
            )
        } returns emptyList()

        val expectedException: Exception = assertThrows {
            deviceRepository.getDeviceByUuid(uuid.toString())
        }
        assertThat(expectedException).isInstanceOf(ItemNotFoundException::class.java)
        assertThat(expectedException.message).isEqualTo("Item not found")
    }

    @Test
    fun shouldUseDeviceMapperAsRowMapper() {
        every {
            jdbcTemplate.query(
                getDeviceByUuidQuery,
                any<DeviceMapper>(),
                uuid.toString()
            )
        } returns mutableListOf(mockk<Device>())
    }

    @Test
    fun shouldMapDeviceViaDeviceMapper() {
        val existingDevice: Device = deviceToBeCreated.copy(uuid = uuid.toString())

        val resultSet = mockk<ResultSet> {
            every {
                getString("uuid")
            } returns existingDevice.uuid
            every {
                getString("serialNumber")
            } returns existingDevice.serialNumber
            every {
                getString("phoneNumber")
            } returns existingDevice.phoneNumber
            every {
                getString("model")
            } returns existingDevice.model
        }

        assertThat(DeviceMapper().mapRow(resultSet, anyInt())).isEqualTo(existingDevice)
    }
}

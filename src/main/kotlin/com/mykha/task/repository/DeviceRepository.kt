package com.mykha.task.repository

import com.mykha.task.model.Device
import com.mykha.task.model.DeviceAssignment
import com.mykha.task.tool.UuidGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class DeviceRepository(val jdbcTemplate: JdbcTemplate, val uuidGenerator: UuidGenerator) {
    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(DeviceRepository::class.java)
    }

    fun createDevice(device: Device): String {
        device.uuid = uuidGenerator.generateUuid().toString()
        logger.debug("debug {}", device)
        jdbcTemplate.update(
            "insert into devices (uuid, serialNumber, phoneNumber, model) values (?,?,?,?)",
            device.uuid, device.serialNumber, device.phoneNumber, device.model
        )
        return device.uuid!!
    }

    fun getDeviceByUuid(uuid: String): Device {
        val results: List<Device> = jdbcTemplate.query(
            "select uuid, serialNumber, phoneNumber, model, userId from devices where uuid=?",
            DeviceMapper(),
            uuid
        )

        logger.debug("debug {}", results)
        if (results.isEmpty()) {
            logger.warn("Device with uuid:{} wasn't found", uuid)
            throw ItemNotFoundException()
        }
        return results.first()
    }

    fun getDevicesByUserId(userId: String): List<Device> {
        val devices: List<Device> = jdbcTemplate.query(
            "select uuid, serialNumber, phoneNumber, model, userId from devices where userId=?",
            DeviceMapper(),
            userId
        )
        logger.debug("getDevicesByUserId:{}", devices)
        return devices
    }

    fun assignDevice(deviceAssignment: DeviceAssignment) {
        val updatedRows = jdbcTemplate.update(
            "update devices set userId=? where uuid=?",
            deviceAssignment.userId, deviceAssignment.deviceUuid
        )
        if (updatedRows == 0) {
            throw ItemNotFoundException("Device wasn't found by provided uuid: ${deviceAssignment.deviceUuid}")
        }
    }
}

class DeviceMapper: RowMapper<Device> {
    override fun mapRow(resultSet: ResultSet, rowNum: Int): Device =
        Device(
            uuid = resultSet.getString("uuid"),
            serialNumber = resultSet.getString("serialNumber"),
            phoneNumber = resultSet.getString("phoneNumber"),
            model = resultSet.getString("model"),
            userId = resultSet.getString("userId")
        )
}

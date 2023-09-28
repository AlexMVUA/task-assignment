package com.mykha.task.repository

import com.mykha.task.model.Device
import com.mykha.task.tool.UuidGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
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
            "insert into devices (uuid, serialNumber, phoneNumber, model) values ( ?, ? ,?,?)",
            device.uuid, device.serialNumber, device.phoneNumber, device.model
        )
        return device.uuid!!
    }

    fun getDeviceByUuid(uuid: String): Device {
        val result: List<Device> = jdbcTemplate.query(
            "select * from devices where uuid=?", { resultSet, _ ->
                map(resultSet)
            },
            uuid
        )

        logger.debug("debug {}", result)
        return result.first()
    }

    private fun map(resultSet: ResultSet): Device =
        Device(
            uuid = resultSet.getString("uuid"),
            serialNumber = resultSet.getString("serialNumber"),
            phoneNumber = resultSet.getString("phoneNumber"),
            model = resultSet.getString("model")
        )
}

package de.saefty.monitoring.machinemonitoring.csv

import com.influxdb.client.InfluxDBClient
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import de.saefty.monitoring.machinemonitoring.config.influxdb.InfluxDbConfigurationProperties
import de.saefty.monitoring.machinemonitoring.machine.MachineService
import de.saefty.monitoring.machinemonitoring.machine.MachineTelemetryDto
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-data")
class DataLoaderTest {

    @MockBean
    lateinit var machineService: MachineService

    @MockBean
    lateinit var mockConfig: InfluxDbConfigurationProperties

    @MockBean
    lateinit var influxDBClient: InfluxDBClient

    @Test
    fun `Given test data, When loading test data, Then data points are stored once per machine`() {
        val machineEntryCaptor = argumentCaptor<MachineTelemetryDto>()
        val machineNameCaptor = argumentCaptor<String>()

        verify(machineService, times(3)).createNewMachine(
            machineEntryCaptor.capture(),
            machineNameCaptor.capture()
        )

        machineEntryCaptor.allValues.forEach {
            assertTrue(it.parameters.entries.isNotEmpty())
        }
        machineNameCaptor.allValues.forEach {
            assertTrue(it.isNotEmpty())
        }
    }
}

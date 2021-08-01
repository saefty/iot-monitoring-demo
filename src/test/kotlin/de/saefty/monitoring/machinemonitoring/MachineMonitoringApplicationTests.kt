package de.saefty.monitoring.machinemonitoring

import com.influxdb.client.InfluxDBClient
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(classes = [MachineMonitoringApplication::class])
class MachineMonitoringApplicationTests {
    @MockBean
    lateinit var influxDBClient: InfluxDBClient


    @Test
    fun contextLoads() {
        // Ensure spring boot context starts
    }

}

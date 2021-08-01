package de.saefty.monitoring.machinemonitoring.machine

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.influxdb.client.InfluxDBClient
import com.nhaarman.mockitokotlin2.any
import de.saefty.monitoring.machinemonitoring.MachineMonitoringApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    classes = [MachineMonitoringApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class MachineControllerWebMvcTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var machineService: MachineService

    @MockBean
    lateinit var influxDBClient: InfluxDBClient

    @BeforeEach
    fun resetMock() {
        Mockito.reset(machineService)
    }

    val testMachineData = (0..10).map {
        MachineMetrics(
            "machine$it",
            "Machine$it",
            listOf(Parameter("some_parameter", it.toDouble()))
        )
    }.toSet()

    @Test
    fun `Given machine Data, When latest data is fetched, Then data is returned as json`() {
        `when`(machineService.fetchAllLatestMachineData()).thenReturn(testMachineData)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/machines")
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val responseList: Set<MachineMetrics> = objectMapper.readValue(result.response.contentAsString)
        assertEquals(testMachineData, responseList)
    }

    @Test
    fun `Given machine metrics, When metrics are fetched, Then data is returned as json`() {
        `when`(machineService.readMachineMetrics(any(), any(), any())).thenReturn(testMachineData)
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/machines/metrics")
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val responseList: Set<MachineMetrics> = objectMapper.readValue(result.response.contentAsString)
        assertEquals(testMachineData, responseList)
    }

    @Test
    fun `Given machines, When new machine telemetry is submitted, Then data is stored`() {
        `when`(machineService.fetchAllLatestMachineData()).thenReturn(testMachineData)
        `when`(machineService.saveMachineTelemetry(any())).thenReturn(testMachineData.first())

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/machines/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "machineKey": "${testMachineData.first().machineKey}",
                            "parameters": {
                                "core_diameter": 3,
                                "speed": 20
                            }
                        }
                    """.trimIndent()
                )
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
        val response: MachineMetrics = objectMapper.readValue(result.response.contentAsString)
        assertEquals(testMachineData.first(), response)
    }

    @Test
    fun `Given no machines, When new machine telemetry is submitted, Then error is returned`() {
        `when`(machineService.fetchAllLatestMachineData()).thenReturn(setOf())
        `when`(machineService.saveMachineTelemetry(any())).thenCallRealMethod()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/machines/metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "machineKey": "${testMachineData.first().machineKey}",
                            "parameters": {
                                "core_diameter": 3,
                                "speed": 20
                            }
                        }
                    """.trimIndent()
                )
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}

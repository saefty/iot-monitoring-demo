package de.saefty.monitoring.machinemonitoring.machine

import com.influxdb.Cancellable
import com.influxdb.query.FluxTable
import com.influxdb.query.internal.FluxCsvParser
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import java.time.Instant
import okio.Buffer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`

class MachineServiceTest {

    private val machineRepository: MachineRepository = mock { }


    private val mockFluxTable: FluxCsvParser = FluxCsvParser()

    val mockFluxCsvConsumerTableLastMachineData: List<FluxTable> = mockFluxTable.parseFile("/last_machines.csv").tables
    val mockFluxCsvConsumerTableMeanMachineData: List<FluxTable> = mockFluxTable.parseFile("/mean_machines.csv").tables


    private val classUnderTest = MachineService(machineRepository)

    @Test
    fun `Given machine flux table, when data is retrieved, Then data is mapped correctly`() {
        `when`(machineRepository.readLastMachineData()).thenReturn(
            mockFluxCsvConsumerTableLastMachineData
        )
        // NOTE: assertion based on the test data provided
        assertEquals(3, classUnderTest.fetchAllLatestMachineData().size)
    }

    @Test
    fun `Given machine flux table with means, when data is retrieved, Then data is mapped correctly`() {
        `when`(machineRepository.readMachineMetricsAggregated(any(), any(), any())).thenReturn(
            mockFluxCsvConsumerTableMeanMachineData
        )
        // NOTE: assertion based on the test data provided
        assertEquals(3, classUnderTest.readMachineMetrics(Instant.now(), Instant.now(), DataOperation.MEAN).size)
    }

    companion object {
        private fun FluxCsvParser.parseFile(name: String): FluxCsvParser.FluxResponseConsumerTable {
            val target = FluxResponseConsumerTable()
            parseFluxResponse(
                Buffer().also {
                    it.readFrom(
                        MachineServiceTest::class.java.getResource(name).openStream()
                    )
                },
                (object : Cancellable {
                    override fun cancel(){
                        // Not needed for mocks
                    }
                    override fun isCancelled(): Boolean = false
                }),
                target
            )
            return target;
        }
    }
}

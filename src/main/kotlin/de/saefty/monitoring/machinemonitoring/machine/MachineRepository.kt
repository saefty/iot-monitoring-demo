package de.saefty.monitoring.machinemonitoring.machine

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import com.influxdb.query.FluxTable
import com.influxdb.query.dsl.Flux
import com.influxdb.query.dsl.functions.restriction.Restrictions
import de.saefty.monitoring.machinemonitoring.config.influxdb.median
import io.micrometer.core.annotation.Timed
import java.time.Clock
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MachineRepository(
    private val influxDBClient: InfluxDBClient,
    private val clock: Clock
) {

    companion object {
        private val logger = LoggerFactory.getLogger(MachineRepository::class.java)

        private const val MEASUREMENT_KEY = "machine_telemetry"

        private fun groupedByMachinesBaseQuery(start: Instant? = null, stop: Instant? = null) =
            Flux.from("machinery").let {
                if (stop != null && start != null)
                    it.range(start, stop)
                else if (start != null)
                    it.range(start)
                else it
            }.filter(
                Restrictions.measurement().equal(MEASUREMENT_KEY)
            ).groupBy(listOf("_field", "machineKey", "machineName"))
    }

    /**
     * Stores a new telemetry data point at the current system time.
     * @param machineTelemetryDto parameters and machine key.
     * @param machineName human readable name of the machine.
     */
    @Timed("app.telemetry.write")
    fun writeDataPoint(machineTelemetryDto: MachineTelemetryDto, machineName: String) {
        val point = Point(MEASUREMENT_KEY)
        machineTelemetryDto.parameters.forEach { (t, u) ->
            point.addField(t, u)
            point.addTag("machineKey", machineTelemetryDto.machineKey)
            point.addTag("machineName", machineName)
        }
        point.time(clock.instant(), WritePrecision.MS)

        influxDBClient.writeApiBlocking.writePoint(
            point
        )
    }

    /**
     * Reads all data for known, existing machines and returns the latest parameters in a FluxTable
     */
    @Timed("app.telemetry.read")
    fun readLastMachineData(): List<FluxTable> {
        val queryString = groupedByMachinesBaseQuery(start = Instant.EPOCH).last().toString()
        logger.trace(queryString)

        return influxDBClient.queryApi.query(queryString)
    }

    /**
     * Reads data from the defined time range and applies [DataOperation] on it
     * @param start start of the time range
     * @param stop end of the time range
     */
    @Timed("app.telemetry.calculate")
    fun readMachineMetricsAggregated(
        start: Instant,
        stop: Instant,
        operation: DataOperation
    ): List<FluxTable> {
        val queryFlux = groupedByMachinesBaseQuery(start = start, stop = stop)
        val queryString = when (operation) {
            DataOperation.MEAN -> queryFlux.mean().toString()
            DataOperation.MEDIAN -> queryFlux.median()
            DataOperation.MAX -> queryFlux.max().toString()
            DataOperation.MIN -> queryFlux.min().toString()
        }
        logger.trace(queryString)

        return influxDBClient.queryApi.query(
            queryString
        )
    }
}

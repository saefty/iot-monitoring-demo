package de.saefty.monitoring.machinemonitoring.machine

import de.saefty.monitoring.machinemonitoring.exception.MachineNotFoundException
import java.time.Instant
import org.springframework.stereotype.Service

@Service
class MachineService(
    private val machineRepository: MachineRepository
) {

    /**
     * Stores a new machine into the system
     */
    fun createNewMachine(entry: MachineTelemetryDto, name: String) {
        machineRepository.writeDataPoint(entry, name)
    }

    /**
     * Stores new machine telemetry into the system.
     * Throws bad request if machine does not exist
     * @param entry new machine telemetry
     * @throws MachineNotFoundException
     */
    fun saveMachineTelemetry(entry: MachineTelemetryDto): MachineMetrics? {
        val machineEntry = fetchAllLatestMachineData().find { it.machineKey == entry.machineKey }
            ?: throw MachineNotFoundException("${entry.machineKey} is a not known machine.")
        machineRepository.writeDataPoint(entry, machineEntry.name)
        return fetchAllLatestMachineData().find { it.machineKey == entry.machineKey }
    }

    /**
     * Retrieves and maps the latest machine data from influxDb
     */
    fun fetchAllLatestMachineData(): Set<MachineMetrics> {
        val dataChannel = machineRepository.readLastMachineData().map {
            it.records.map { record ->
                MachineMetrics(
                    record.values["machineKey"]!! as String,
                    record.values["machineName"]!! as String,
                    listOf(
                        ParameterInstant(
                            record.field as String,
                            record.value!! as Double,
                            record.time!!
                        )
                    )

                )
            }
        }.flatten()

        return dataChannel.joinData().toSet()
    }

    /**
     * Retrieves and maps the machine data based on the [DataOperation] from influxDb
     */
    fun readMachineMetrics(start: Instant, end: Instant, operation: DataOperation): Set<MachineMetrics> {
        val influxData = machineRepository.readMachineMetricsAggregated(start, end, operation)
            .map {
                it.records.map { record ->
                    MachineMetrics(
                        record.values["machineKey"]!! as String,
                        record.values["machineName"] as? String ?: "",
                        listOf(
                            ParameterRange(
                                record.values["_field"]!! as String,
                                record.values["_value"]!! as Double,
                                record.values["_start"]!! as Instant,
                                record.values["_stop"]!! as Instant,
                            )
                        )

                    )
                }
            }.flatten()

        return influxData.joinData().toSet()
    }

    /**
     * Reduces a list of single stat [MachineMetrics] to one object per machine key.
     */
    fun List<MachineMetrics>.joinData(): List<MachineMetrics> {
        return this.groupBy {
            it.machineKey
        }.map {
            MachineMetrics(it.key, it.value.first().name, it.value.map { it.parameters }.flatten())
        }
    }
}

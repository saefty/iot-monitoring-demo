package de.saefty.monitoring.machinemonitoring.csv

import com.influxdb.client.InfluxDBClient
import de.saefty.monitoring.machinemonitoring.config.influxdb.InfluxDbConfigurationProperties
import de.saefty.monitoring.machinemonitoring.machine.MachineService
import de.saefty.monitoring.machinemonitoring.machine.MachineTelemetryDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
@Profile("test-data")
class DataLoader(
    private val influxDBClient: InfluxDBClient,
    private val influxDbConfigurationProperties: InfluxDbConfigurationProperties,
    private val machineCsvFileSource: CsvFileSource<MachineCsvDto>,
    private val parameterCsvDto: CsvFileSource<ParameterCsvDto>,
    private val machineService: MachineService
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DataLoader::class.java)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun load() {
        runCatching {
            cleanUpBucket()
        }

        logger.info("Starting CSV data import")


        val machines = machineCsvFileSource.loadListOfObjects(MachineCsvDto::class.java)
        val parameters = parameterCsvDto.loadListOfObjects(ParameterCsvDto::class.java).groupBy { it.machineKey }

        for (machine in machines) {
            machineService.createNewMachine(
                MachineTelemetryDto(
                    machine.key,
                    mapOf(*parameters.getOrDefault(machine.key, listOf()).map { Pair(it.key, it.value) }.toTypedArray())
                ),
                machine.name
            )
        }
        logger.trace("found machines {}", machines)
        logger.trace("found parameters {}", parameters)
    }

    private fun cleanUpBucket() {
        logger.info("Cleaning Bucket")
        runCatching {
            influxDBClient.bucketsApi.findBucketByName(influxDbConfigurationProperties.machineTelemetryBucket).also {
                influxDBClient.bucketsApi.deleteBucket(it!!)
            }
        }

        val org = influxDBClient.organizationsApi.findOrganizations().find {
            it.name == influxDbConfigurationProperties.org
        }!!

        influxDBClient.bucketsApi.createBucket(
            influxDbConfigurationProperties.machineTelemetryBucket,
            org
        )
    }
}

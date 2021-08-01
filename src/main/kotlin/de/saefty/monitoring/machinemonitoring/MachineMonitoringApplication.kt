package de.saefty.monitoring.machinemonitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MachineMonitoringApplication

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<MachineMonitoringApplication>(*args)
}

package de.saefty.monitoring.machinemonitoring.machine

import java.time.Instant
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Handles fetching of machine data and deals with writing new data points into the system
 */
@RestController
class MachineController(
    private val machineService: MachineService
) {

    @PostMapping("/machines/metrics")
    fun processMachineTelemetry(@RequestBody body: MachineTelemetryDto): MachineMetrics? {
        return machineService.saveMachineTelemetry(body)
    }

    @GetMapping("/machines")
    fun getMachines(): Set<MachineMetrics> {
        return machineService.fetchAllLatestMachineData()
    }

    @GetMapping("/machines/metrics")
    fun getMetrics(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: Instant?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) stop: Instant?,
        @RequestParam operation: DataOperation?
    ): Set<MachineMetrics> {
        return machineService.readMachineMetrics(
            start ?: Instant.EPOCH,
            stop ?: Instant.now(),
            operation ?: DataOperation.MEAN
        )
    }
}

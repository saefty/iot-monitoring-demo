package de.saefty.monitoring.machinemonitoring.machine

/**
 * Represents a new set of machine telemetry
 */
data class MachineTelemetryDto(
    val machineKey: String,
    val parameters: Map<String, Double>
)

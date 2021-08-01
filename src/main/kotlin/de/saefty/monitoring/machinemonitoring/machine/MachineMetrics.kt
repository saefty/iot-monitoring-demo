package de.saefty.monitoring.machinemonitoring.machine

import java.time.Instant

/**
 * Represents a single machine and a list of parameters
 */
data class MachineMetrics(
    val machineKey: String,
    val name: String,
    val parameters: List<Parameter>
)

/**
 * Represents a parameter
 */
open class Parameter(
    val name: String,
    val value: Double
) {
    /**
     * NOTE: Usually this would be solved with data classes
     * Data classes do not support inheritance. This is the quickest solution
     * If such logic is needed more frequently lombock could be used.
     */
    override fun equals(other: Any?): Boolean {
        return other is Parameter && other.name == name && other.value == value
    }

    /**
     * Auto generated
     */
    @SuppressWarnings("MagicNumber")
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}

/**
 * Represents a parameter at a given time
 */
class ParameterInstant(
    name: String,
    value: Double,
    val timestamp: Instant,
) : Parameter(name, value)

/**
 * Represents a parameter within a time range
 */
class ParameterRange(
    name: String,
    value: Double,
    val start: Instant,
    val stop: Instant
) : Parameter(name, value)

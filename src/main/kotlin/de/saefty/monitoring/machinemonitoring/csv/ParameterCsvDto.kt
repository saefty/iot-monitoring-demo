package de.saefty.monitoring.machinemonitoring.csv

import com.fasterxml.jackson.annotation.JsonProperty

data class ParameterCsvDto(
    @JsonProperty(value = "machine_key")
    val machineKey: String = "",
    @JsonProperty("key")
    val key: String = "",
    @JsonProperty("value")
    val value: Double = 0.0
)

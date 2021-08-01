package de.saefty.monitoring.machinemonitoring.csv

import com.fasterxml.jackson.annotation.JsonProperty

data class MachineCsvDto(
    @JsonProperty("key")
    val key: String = "",
    @JsonProperty("name")
    val name: String = ""
)

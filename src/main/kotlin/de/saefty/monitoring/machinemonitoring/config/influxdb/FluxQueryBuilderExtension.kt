package de.saefty.monitoring.machinemonitoring.config.influxdb

import com.influxdb.query.dsl.Flux

fun Flux.median(): String {
    return "$this |> median()"
}

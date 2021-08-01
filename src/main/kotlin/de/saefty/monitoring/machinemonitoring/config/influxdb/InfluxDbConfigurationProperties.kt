package de.saefty.monitoring.machinemonitoring.config.influxdb

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("spring.influx")
@ConstructorBinding
data class InfluxDbConfigurationProperties(
    val token: String,
    val url: String,
    val org: String,
    val machineTelemetryBucket: String,
    val monitoringBucket: String
)

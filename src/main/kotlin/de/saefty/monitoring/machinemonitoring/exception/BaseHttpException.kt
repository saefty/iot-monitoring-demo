package de.saefty.monitoring.machinemonitoring.exception

data class BaseHttpException(
    val status: Int,
    val error: String
)

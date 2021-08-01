package de.saefty.monitoring.machinemonitoring.config.csv

import de.saefty.monitoring.machinemonitoring.csv.CsvFileSource
import de.saefty.monitoring.machinemonitoring.csv.MachineCsvDto
import de.saefty.monitoring.machinemonitoring.csv.ParameterCsvDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CsvContextConfiguration {
    @Bean
    fun machineCsvFileSource(): CsvFileSource<MachineCsvDto> {
        return CsvFileSource("./BE_TestData/machines.csv")
    }

    @Bean
    fun parametersCsvFileSource(): CsvFileSource<ParameterCsvDto> {
        return CsvFileSource("./BE_TestData/parameters.csv")
    }
}

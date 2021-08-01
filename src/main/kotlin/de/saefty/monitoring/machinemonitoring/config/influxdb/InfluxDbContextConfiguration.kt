package de.saefty.monitoring.machinemonitoring.config.influxdb


import com.influxdb.LogLevel
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.domain.Organization
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableConfigurationProperties(InfluxDbConfigurationProperties::class)
@Profile("influxdb-connection")
class InfluxDbContextConfiguration {
    @Bean
    fun connectionFactory(properties: InfluxDbConfigurationProperties): InfluxDBClient {
        val influxDB = InfluxDBClientFactory.create(
            properties.url, properties.token.toCharArray(), properties.org, properties.machineTelemetryBucket
        );
        val org = getOrCreateOrg(influxDB, properties.org)

        createManagementBucket(influxDB, org, properties.monitoringBucket)

        influxDB.logLevel = LogLevel.BASIC
        return influxDB
    }

    private fun createManagementBucket(influxDBClient: InfluxDBClient, org: Organization, bucketName: String) {
        if (influxDBClient.bucketsApi.findBucketByName(bucketName) == null)
            influxDBClient.bucketsApi.createBucket(bucketName, org)
    }

    private fun getOrCreateOrg(influxDBClient: InfluxDBClient, orgName: String): Organization {
        var org = influxDBClient.organizationsApi.findOrganizations()
            .find { it.name == orgName }
        if (org == null) {
            org = influxDBClient.organizationsApi.createOrganization(orgName)
        }
        return org
    }
}

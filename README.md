## How to run the application

1. docker-compose up, wait for influxdb to start up.
2. `./gradlew bootRun` or use IntelliJ


Note: Influx UI can be accessed at `http://localhost:8086`.

## Test

* The repository has not been tested further due too complexity of the InfluxDb layer.
* Test coverage report is generated after running `./gradlew test`. It can be found in `build/reports/jacoco`

### Usage:

*GET latest machines*

```shell script
curl --location --request GET 'http://localhost:8080/machines'
```

*GET /machines/metrics, fetches data in the time range based on the operation, query parameters:*

* start, iso8601 timestamp, optional, see below. Defaults to Instant.EPOCH
* stop, iso8601 timestamp, optional, see below. Defaults to Instant.now()
* operation, optional. Defaults to "MEDIAN". Possible values: MIN, MAX, MEDIAN, MEAN

```shell script
curl --location --request GET 'http://localhost:8080/machines/metrics?start=2021-07-31T20:28:00.000Z&stop=2021-07-31T23:30:00.000Z&operation=MAX'
```

*POST /machines/metrics, store new latest machine telemetry*

* machineKey, see BE_TestData/machines.csv. Only machine keys loaded by the test system can be updated.
* parameters, any key value pair<String, Double>
* returns the lastest machine data for the machineKey including the new data.

````shell script
curl --location --request GET 'http://localhost:8080/machines/metrics' \
--header 'Content-Type: application/json' \
--data-raw '{
    "machineKey": "ajoparametrit",
    "parameters": {
        "core_diameter": 3,
        "speed": 20.0
    }
}'
````

---
## Brain Storming and thoughts:

* Persistence layer for time series data needed
* I checked how prometheus works and some documentation about that 
  and found InfluxDB very fitting
* fast access and supportive for time series calculations --> InfluxDB might be a good choice.


## Decisions Log
* Use InfluxDB, Have Grafana Dashboard for debugging. 
  Setup used from here: https://github.com/jkehres/docker-compose-influxdb-grafana/blob/master/docker-compose.yml
* Use Kotlin / Spring Boot as most familiar with this
* Prevent any other persistence layer to reduce complexity.

## Production Readiness
* The spring boot application stores application metrics also in influxdb.
* A Dockerfile was not added but would be required for running on a cloud provider
* Structured logging / json logging missing in order to have machine readable logs.
* Health check is not yet exported
* Missing tests of data access layer should be added.
* More red path tests for failed influxdb connections and failure handling in the data model
  i.e. unexpected value type should be added.
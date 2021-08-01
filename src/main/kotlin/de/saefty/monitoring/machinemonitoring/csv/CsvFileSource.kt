package de.saefty.monitoring.machinemonitoring.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import java.io.File
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CsvFileSource<T>(private val fileName: String) {
    /**
     * Reads all objects from the csv file
     */
    fun loadListOfObjects(type: Class<T>): List<T> {
        return CsvUtils.loadListOfObjects(type, this.fileName)
    }

    companion object CsvUtils {
        private val logger: Logger = LoggerFactory.getLogger(CsvUtils::class.java)

        fun <T> loadListOfObjects(type: Class<T>, fileName: String): List<T> {
            logger.info("Load list of objects from $fileName")

            val csvFile = File(fileName)
            val mapper = CsvMapper()
            val schema = mapper.schemaWithHeader()

            val it = mapper.readerFor(type)
                .with(schema)
                .readValues<T>(csvFile)

            return it.readAll()
        }
    }
}

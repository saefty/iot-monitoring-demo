package de.saefty.monitoring.machinemonitoring.config

import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringContextConfiguration {
    @Bean
    fun clock(): Clock {
        return Clock.systemUTC();
    }
}

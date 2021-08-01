import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("io.gitlab.arturbosch.detekt") version "1.17.1"

	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
	kotlin("plugin.jpa") version "1.5.21"
	id("jacoco")

}

group = "de.saefty.monitoring"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")


	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-influx")

	implementation("com.influxdb:influxdb-client-java:3.1.0")
	implementation("com.influxdb:flux-dsl:3.1.0")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito:mockito-inline:3.11.2")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}

detekt {
	input = files("src/main/kotlin", "src/test/kotlin")
	parallel = false
	config = files("detekt-config.yml")
	disableDefaultRuleSets = false
	debug = false
	reports {
		html {
			enabled = true
			destination =
				file("$buildDir/reports/detekt/detekt.html")
		}
	}
}


tasks {
	test {
		finalizedBy(jacocoTestReport)
	}
	jacoco {
		toolVersion = "0.8.7"
	}
	jacocoTestReport {
		finalizedBy(jacocoTestCoverageVerification)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

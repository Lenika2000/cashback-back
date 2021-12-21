package ru.itmo.bllab1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.kafka.annotation.EnableKafka

@PropertySource("classpath:kafka.properties")
@SpringBootApplication
@EnableKafka
class BlLab1Application

fun main(args: Array<String>) {
    runApplication<BlLab1Application>(*args)
}

package com.trivago.spiderman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpidermanApplication

fun main(args: Array<String>) {
    runApplication<SpidermanApplication>(*args)
}

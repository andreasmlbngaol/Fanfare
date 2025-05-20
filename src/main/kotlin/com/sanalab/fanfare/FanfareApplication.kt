package com.sanalab.fanfare

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FanfareApplication

fun main(args: Array<String>) {
    runApplication<FanfareApplication>(*args)
}

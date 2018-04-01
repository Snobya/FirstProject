package com.avant

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class App

fun main(args: Array<String>) {
	runApplication<App>()
}

/*

TODO Global tasks

2. Person tracker (for users who bought offer) - possible later
3. Optimize Mongo queries - in OrderModel.
4. Render special pages for CEO
5. Turn on Auth

Future: Facebook photos (due to inavailability of APIs)

 */
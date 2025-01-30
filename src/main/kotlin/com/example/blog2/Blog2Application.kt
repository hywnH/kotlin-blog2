package com.example.blog2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
class Blog2Application

fun main(args: Array<String>) {
	runApplication<Blog2Application>(*args)
}

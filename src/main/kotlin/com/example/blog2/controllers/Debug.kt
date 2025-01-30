package com.example.blog2.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/debug")
class DebugController(
    @Value("\${DATABASE_URL:}") private val databaseUrl: String
) {
    @GetMapping
    fun debug(): String {
        return "DATABASE_URL: $databaseUrl"
    }
}

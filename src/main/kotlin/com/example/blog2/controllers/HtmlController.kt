package com.example.blog2.controllers

import com.example.blog2.BlogProperties
import com.example.blog2.entities.Article
import com.example.blog2.entities.User
import com.example.blog2.repositories.ArticleRepository
import com.example.blog2.repositories.UserRepository
import com.example.blog2.services.ArticleService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Controller
class HtmlController(private val properties: BlogProperties,
                     private val articleController: ArticleController
) {

    @GetMapping("/home")
    fun blog(@SessionAttribute("user") username: String?, model: Model): String {
        model["title"] = properties.title
        model["banner"] = properties.banner
        model["articles"] = articleController.getRenderedArticles(username)
        model["user"] = username
        return "blog"
    }

}
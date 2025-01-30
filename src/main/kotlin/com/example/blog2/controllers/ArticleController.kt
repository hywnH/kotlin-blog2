package com.example.blog2.controllers

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
class ArticleController(private val articleService: ArticleService,
                        private val articleRepository: ArticleRepository,
                        private val userRepository: UserRepository
) {

    @GetMapping("/write")
    fun writeArticle(@SessionAttribute("user") username: String?, model: Model): String {
        if (username == null) return "redirect:/login"

        model["title"] = "Write a Post"
        return "write"
    }

    @PostMapping("/write")
    fun createArticle(
        @SessionAttribute("user") username: String?,
        @RequestParam title: String,
        @RequestParam content: String
    ): String {
        if (username == null) return "redirect:/login"

        val user = userRepository.findByUsername(username)
            ?: return "redirect:/login"

        articleService.createArticle(title, content, user)
        return "redirect:/home"
    }

    @PostMapping("/delete")
    fun deleteArticle(
        @RequestParam articleId: Long,
        @SessionAttribute(name = "user", required = false) username: String?,
        model: Model
    ): String {
        if (username == null) {
            return "redirect:/login"
        }

        val success = articleService.deleteArticle(articleId, username)
        if (!success) {
            model["error"] = "You are not authorized to delete this article."
        }

        return "redirect:/home"
    }

    @GetMapping("/article/new")
    fun newArticleForm(model: Model): String {
        model["title"] = "Create New Article"
        return "write" // write.mustache 템플릿
    }

    @GetMapping("/article/{slug}")
    fun article(
        @PathVariable slug: String,
        @SessionAttribute(name = "user", required = false) username: String?,
        model: Model
    ): String {
        val article = articleRepository
            .findBySlug(slug)
            ?.render(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This article does not exist")
        model["title"] = article.title
        model["article"] = article
        return "article"
    }

    @PostMapping("/article")
    fun createArticle(
        @RequestParam title: String,
        @RequestParam content: String,
        @RequestParam author: Long
    ): String {
        val user = userRepository.findById(author)
            .orElseThrow { IllegalArgumentException("Author not found") }

        val article = Article(
            title = title,
            content = content,
            author = user
        )
        articleRepository.save(article)

        return "redirect:/home" // 저장 후 메인 페이지로 리다이렉트
    }

    fun getRenderedArticles(username: String?): List<RenderedArticle>
    = articleRepository.findAllByOrderByCreatedAtDesc().map { it.render(username) }

    fun Article.render(@SessionAttribute(name = "user", required = false) username: String?) = RenderedArticle(
        slug,
        id.toString(),
        title,
        content,
        author,
        createdAt.toString(),
        (this.author.username == username)
    )

    data class RenderedArticle(
        val slug: String,
        val id: String,
        val title: String,
        val content: String,
        val author: User,
        val createdAt: String,
        val isAuthor: Boolean
    )
}

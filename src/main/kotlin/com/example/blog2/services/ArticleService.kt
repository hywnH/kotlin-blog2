package com.example.blog2.services

import com.example.blog2.entities.Article
import com.example.blog2.entities.User
import com.example.blog2.repositories.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(private val articleRepository: ArticleRepository) {

    fun createArticle(title: String, content: String, author: User): Article {
        val article = Article(title = title, content = content, author = author)
        return articleRepository.save(article)
    }

    fun deleteArticle(articleId: Long, username: String): Boolean {
        val article = articleRepository.findById(articleId).orElse(null) ?: return false
        // 작성자와 현재 사용자가 일치하는지 확인
        return if (article.author.username == username) {
            articleRepository.delete(article)
            true
        } else {
            false
        }
    }
}
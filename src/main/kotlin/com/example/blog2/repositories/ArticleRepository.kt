package com.example.blog2.repositories

import com.example.blog2.entities.Article
import org.springframework.data.repository.CrudRepository

interface ArticleRepository : CrudRepository<Article, Long> {
    fun findBySlug(slug: String): Article?
    fun findAllByOrderByCreatedAtDesc(): List<Article>
}
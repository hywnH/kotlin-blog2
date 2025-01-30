package com.example.blog2.entities

import com.example.blog2.toSlug
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "articles")
data class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
    @ManyToOne
    val author: User,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var slug: String = title.toSlug(),
)
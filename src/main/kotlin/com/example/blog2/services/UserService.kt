package com.example.blog2.services

import com.example.blog2.entities.User
import com.example.blog2.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    private val passwordEncoder = BCryptPasswordEncoder()

    fun signup(username: String, email: String, password: String): User {
        val hashedPassword = passwordEncoder.encode(password)
        val user = User(username = username, email = email, password = hashedPassword)
        return userRepository.save(user)
    }

    fun authenticate(username: String, password: String): Boolean {
        val user = userRepository.findByUsername(username) ?:
            throw IllegalArgumentException("No such user")

        return passwordEncoder.matches(password, user.password)
    }
}
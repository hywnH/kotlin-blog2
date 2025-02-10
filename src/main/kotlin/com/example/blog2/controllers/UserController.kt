package com.example.blog2.controllers

import com.example.blog2.entities.User
import com.example.blog2.services.UserService
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

import org.springframework.http.ResponseEntity
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*

@Controller
class UserController(private val userService: UserService) {

    @GetMapping("/signup")
    fun signupPage(): String {
        return "signup" // signup.mustache 템플릿
    }

    @PostMapping("/signup")
    fun signup(
        @ModelAttribute user: User,
        model: Model
    ): String {
        try {
            userService.signup(user.username, user.email, user.password)
            return "redirect:/login"
        } catch (e: Exception) {
            model.addAttribute("error", "Signup failed: ${e.message}")
            return "signup"
        }
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "login" // signup.mustache 템플릿
    }

    @PostMapping("/login")
    fun login(
        @RequestParam username: String,
        @RequestParam password: String,
        request: HttpServletRequest,
        model: Model
    ): String {
        try {
            if (userService.authenticate(username, password)) {
                request.session.setAttribute("user", username)
                return "redirect:/home"
            } else {
                model.addAttribute("error", "no such user")
                return "redirect:/login?error=true"
            }
        } catch (e: Exception) {
            model.addAttribute("error", "login failed: ${e.message}")
            return "redirect:/login?error=true"
        }
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.session.invalidate()
        return "redirect:/home"
    }

    @GetMapping("/bank")
    fun bank(@SessionAttribute("user") username: String?, model: Model): String {
        val user = username?.let { userService.getUserByUsername(it) }
        model["title"] = "Banking Services"
        model["user"] = user?.username
        model["balance"] = user?.balance
        return "bank"
    }

}
package com.example.blog2.controllers

import com.example.blog2.entities.User
import com.example.blog2.services.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserController(private val userService: UserService) {

    @GetMapping("/signup")
    fun signupPage(): String {
        return "signup" // signup.mustache 템플릿
    }

//    @PostMapping("/signup")
//    fun signup(
//        @RequestParam username: String,
//        @RequestParam email: String,
//        @RequestParam password: String,
//        model: Model
//    ): String {
//        try {
//            userService.signup(username, email, password)
//            return "redirect:/login"
//        } catch (e: Exception) {
//            model.addAttribute("error", "Signup failed: ${e.message}")
//            return "signup"
//        }
//    }

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
            userService.authenticate(username, password)
            request.session.setAttribute("user", username)
            return "redirect:/home"
        } catch (e: Exception) {
            model.addAttribute("error", "login failed: ${e.message}")
            return "redirect:/login?error=true"
        }
    }

//    @PostMapping("/login")
//    fun login(@ModelAttribute user: User,
//              request: HttpServletRequest,
//              model: Model): String {
//        try {
//            userService.authenticate(user.username, user.password)
//            request.session.setAttribute("user", user.username)
//            return "redirect:/home"
//        } catch (e: Exception) {
//            model.addAttribute("error", "login failed: ${e.message}")
//            return "redirect:/login?error=true"
//        }
//    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.session.invalidate()
        return "redirect:/home"
    }

}
package com.example.blog2.controllers


import com.example.blog2.services.UserService
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/bank")
class BankController(private val userService: UserService) {

    // 입금 (POST 요청, 완료 후 /bank로 리다이렉트)
    @PostMapping("/accounts/deposit")
    fun deposit(
        @SessionAttribute("user") username: String?,
        @RequestParam amount: Double,
        redirectAttributes: RedirectAttributes
    ): String = runBlocking {
        val user = username?.let { userService.getUserByUsername(it) }
            ?: return@runBlocking "redirect:/login"

        return@runBlocking try {
            userService.deposit(user.id, amount)
            "redirect:/bank"
        } catch (e: IllegalArgumentException) {
            redirectAttributes.addFlashAttribute("error", "Deposit failed: ${e.message}")
            "redirect:/bank"
        }
    }

    // 출금 (POST 요청, 완료 후 /bank로 리다이렉트)
    @PostMapping("/accounts/withdraw")
    fun withdraw(
        @SessionAttribute("user") username: String?,
        @RequestParam amount: Double,
        redirectAttributes: RedirectAttributes
    ): String = runBlocking {
        val user = username?.let { userService.getUserByUsername(it) }
            ?: return@runBlocking "redirect:/login"

        return@runBlocking try {
            userService.withdraw(user.id, amount)
            "redirect:/bank"
        } catch (e: IllegalArgumentException) {
            redirectAttributes.addFlashAttribute("error", "Withdraw failed: ${e.message}")
            "redirect:/bank"
        }
    }

    // 송금 (POST 요청, 완료 후 /bank로 리다이렉트)
    @PostMapping("/accounts/transfer")
    fun transfer(
        @SessionAttribute("user") username: String?,
        @RequestParam toUserName: String,
        @RequestParam amount: Double,
        redirectAttributes: RedirectAttributes
    ): String = runBlocking {
        val user = username?.let { userService.getUserByUsername(it) }
            ?: return@runBlocking "redirect:/login"

        val toUser = userService.getUserByUsername(toUserName)
            ?: run {
                redirectAttributes.addFlashAttribute("error", "Transfer failed: wrong recipient")
                return@runBlocking "redirect:/bank"
            }

        return@runBlocking try {
            userService.transfer(user.id, toUser.id, amount)
            "redirect:/bank"
        } catch (e: IllegalArgumentException) {
            redirectAttributes.addFlashAttribute("error", "Transfer failed: ${e.message}")
            "redirect:/bank"
        }
    }
}

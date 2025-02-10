package com.example.blog2.api

import com.example.blog2.services.UserService
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bank")
class BankApiController(private val userService: UserService) {

    // 잔액 조회 (GET /api/bank/accounts/{id}/balance)
    @GetMapping("/accounts/{id}/balance")
    fun getBalance(@PathVariable id: Long) = runBlocking {
        userService.getBalance(id)
    }

    // 입금 (PATCH /api/bank/accounts/{id}/deposit)
    @PatchMapping("/accounts/{id}/deposit")
    fun deposit(@PathVariable id: Long, @RequestBody request: AmountRequest) = runBlocking {
        userService.deposit(id, request.amount)
    }

    // 출금 (PATCH /api/bank/accounts/{id}/withdraw)
    @PatchMapping("/accounts/{id}/withdraw")
    fun withdraw(@PathVariable id: Long, @RequestBody request: AmountRequest) = runBlocking {
        userService.withdraw(id, request.amount)
    }

    // 송금 (POST /api/bank/accounts/{fromId}/transfer/{toId})
    @PostMapping("/accounts/{fromId}/transfer/{toId}")
    fun transfer(
        @PathVariable fromId: Long,
        @PathVariable toId: Long,
        @RequestBody request: AmountRequest
    ) = runBlocking {
        userService.transfer(fromId, toId, request.amount)
    }
}

// JSON 요청을 위한 DTO
data class AmountRequest(val amount: Double)

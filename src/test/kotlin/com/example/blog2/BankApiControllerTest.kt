package com.example.blog2

import com.example.blog2.api.BankApiController
import com.example.blog2.services.UserService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean

@WebMvcTest(BankApiController::class)
class BankApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        runBlocking {
            doReturn(1000.0).`when`(userService).getBalance(1L)
            doReturn(1500.0).`when`(userService).deposit(1L, 500.0)
            doReturn(800.0).`when`(userService).withdraw(1L, 200.0)
        }
    }

    // 잔액 조회 테스트
    @Test
    @WithMockUser(username = "testUser", roles = ["USER"])
    fun `should return account balance`() {
        mockMvc.perform(get("/api/bank/accounts/1/balance"))
            .andExpect(status().isOk)
            .andExpect(content().string("1000.0"))
    }

    // 입금 테스트
    @Test
    @WithMockUser(username = "testUser", roles = ["USER"])
    fun `should deposit amount`() {
        val jsonRequest = """{"amount": 500.0}"""

        mockMvc.perform(patch("/api/bank/accounts/1/deposit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .with(csrf())) // CSRF 보호 우회
            .andExpect(status().isOk)
            .andExpect(content().string("1500.0"))
    }

    // 출금 테스트
    @Test
    @WithMockUser(username = "testUser", roles = ["USER"])
    fun `should withdraw amount`() {
        val jsonRequest = """{"amount": 200.0}"""

        mockMvc.perform(patch("/api/bank/accounts/1/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .with(csrf())) // CSRF 보호 우회
            .andExpect(status().isOk)
            .andExpect(content().string("800.0"))
    }

}

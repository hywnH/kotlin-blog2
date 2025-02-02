package com.example.blog2.services

import com.example.blog2.actors.AccountActor
import com.example.blog2.entities.User
import com.example.blog2.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap

@Service
class UserService(private val userRepository: UserRepository) {

    private val passwordEncoder = BCryptPasswordEncoder()
    // ✅ 사용자 ID별로 AccountActor를 관리하는 맵
    private val accountActors = ConcurrentHashMap<Long, AccountActor>()

    // ✅ 특정 사용자의 AccountActor 가져오기 (없으면 생성)
    private fun getAccountActor(user: User): AccountActor {
        return accountActors.computeIfAbsent(user.id) { AccountActor(user.balance) }
    }

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

    fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    suspend fun getBalance(userId: Long): Double {
        val user = getUserById(userId)
        return getAccountActor(user).getBalance()
    }

    @Transactional
    suspend fun deposit(userId: Long, amount: Double): Double {
        val user = getUserById(userId)
        val actor = getAccountActor(user)

        actor.deposit(amount) // ✅ AccountActor에서 안전하게 입금 처리
        user.balance = actor.getBalance()
        userRepository.save(user)
        return user.balance
    }

    @Transactional
    suspend fun withdraw(userId: Long, amount: Double): Double {
        val user = getUserById(userId)
        val actor = getAccountActor(user)

        val success = actor.withdraw(amount) // ✅ 출금 요청을 Actor가 처리
        if (!success) throw IllegalArgumentException("Insufficient funds")

        user.balance = actor.getBalance()
        userRepository.save(user)
        return user.balance
    }

    @Transactional
    suspend fun transfer(fromUserId: Long, toUserId: Long, amount: Double) {
        val fromUser = getUserById(fromUserId)
        val toUser = getUserById(toUserId)

        val fromActor = getAccountActor(fromUser)
        val toActor = getAccountActor(toUser)

        // ✅ 출금 후 입금 → 하나의 트랜잭션으로 관리
        val success = fromActor.withdraw(amount)
        if (!success) throw IllegalArgumentException("Insufficient funds")

        toActor.deposit(amount)

        // ✅ Actor 업데이트 후 DB 반영
        fromUser.balance = fromActor.getBalance()
        toUser.balance = toActor.getBalance()

        userRepository.save(fromUser)
        userRepository.save(toUser)
    }
}
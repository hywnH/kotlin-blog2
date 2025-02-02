package com.example.blog2.actors

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

// sealed class란 특정 클래스의 묶음(계층)을 제한하는 클래스
//
// 같은 파일 내에서만 하위 클래스를 정의할 수 있음, 외부에서는 확장 불가능
// 클래스 상속을 제한하면서도, Enum보다 유연하게 데이터 구조를 표현할 수 있음

sealed class AccountCommand
data class Deposit(val amount: Double) : AccountCommand()
data class Withdraw(val amount: Double, val response: CompletableDeferred<Boolean>) : AccountCommand()
data class GetBalance(val response: CompletableDeferred<Double>) : AccountCommand()

// Actor를 이용해 동시성을 관리하는 계좌 클래스
class AccountActor(initialBalance: Double) {
    // 코루틴을 실행할 범위 (Dispatchers.Default: 백그라운드에서 실행)
    private val scope = CoroutineScope(Dispatchers.Default)

    @OptIn(ObsoleteCoroutinesApi::class)
    private val accountActor = scope.actor<AccountCommand> {
        var balance = initialBalance

        // 채널에서 들어오는 메시지(명령)를 하나씩 처리
        for (msg in channel) {
            when (msg) {
                is Deposit -> balance += msg.amount
                is Withdraw -> {
                    if (balance >= msg.amount) {
                        balance -= msg.amount
                        msg.response.complete(true)
                    } else {
                        msg.response.complete(false)
                    }
                }
                is GetBalance -> msg.response.complete(balance)
            }
        }
    }

    // 입금 요청 함수 (Actor에게 Deposit 메시지 전송)
    suspend fun deposit(amount: Double) {
        accountActor.send(Deposit(amount))
    }

    // 출금 요청 함수 (Withdraw 메시지를 보내고 결과를 반환받음)
    suspend fun withdraw(amount: Double): Boolean {
        val response = CompletableDeferred<Boolean>() // 출금 결과를 받을 객체 생성
        accountActor.send(Withdraw(amount, response)) // 출금 메시지 전송
        return response.await() // 출금 성공 여부를 기다렸다가 반환
    }

    // 잔액 조회 요청 함수 (GetBalance 메시지를 보내고 잔액을 반환받음)
    suspend fun getBalance(): Double {
        val response = CompletableDeferred<Double>() // 잔액을 받을 객체 생성
        accountActor.send(GetBalance(response)) // 잔액 조회 메시지 전송
        return response.await() // 잔액을 기다렸다가 반환
    }
}

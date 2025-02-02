package com.example.blog2.actors

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

sealed class AccountCommand
data class Deposit(val amount: Double) : AccountCommand()
data class Withdraw(val amount: Double, val response: CompletableDeferred<Boolean>) : AccountCommand()
data class GetBalance(val response: CompletableDeferred<Double>) : AccountCommand()

class AccountActor(initialBalance: Double) {
    private val scope = CoroutineScope(Dispatchers.Default)

    @OptIn(ObsoleteCoroutinesApi::class)
    private val accountActor = scope.actor<AccountCommand> {
        var balance = initialBalance

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

    suspend fun deposit(amount: Double) {
        accountActor.send(Deposit(amount))
    }

    suspend fun withdraw(amount: Double): Boolean {
        val response = CompletableDeferred<Boolean>()
        accountActor.send(Withdraw(amount, response))
        return response.await()
    }

    suspend fun getBalance(): Double {
        val response = CompletableDeferred<Double>()
        accountActor.send(GetBalance(response))
        return response.await()
    }
}

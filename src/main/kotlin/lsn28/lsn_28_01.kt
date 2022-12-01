package lsn28

import kotlinx.coroutines.delay
import kotlin.coroutines.*

object lsn_28_01{
    fun main() {
        testStartCoroutine()
        Thread.sleep(2000L)
    }

    private val block = suspend {
        println("Hello!")
        delay(1000L)
        println("World!")
        "Result"
    }

    private fun testStartCoroutine() {
        val continuation = object : Continuation<String> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resumeWith(result: Result<String>) {
                println("Result is: ${result.getOrNull()}")
            }
        }

        // createCoroutineUnintercepted(completion).intercepted().resume(Unit)
        block.startCoroutine(continuation)
    }
}


package lsn28

import kotlinx.coroutines.delay
import kotlin.coroutines.*

object lsn_28_02 {
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

        // SafeContinuation(createCoroutineUnintercepted(completion).intercepted(), COROUTINE_SUSPENDED)
        val coroutine = block.createCoroutine(continuation)
        coroutine.resume(Unit)
    }
}

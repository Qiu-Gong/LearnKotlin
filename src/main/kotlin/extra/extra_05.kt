package extra

import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

fun main01() = runBlocking {
    val result = getLengthSuspend("Kotlin")
    println(result)
}

suspend fun getLengthSuspend(text: String): Int = suspendCoroutine { continuation ->
    thread {
        Thread.sleep(1000L)
        continuation.resume(text.length)
    }
}
/*
输出结果：
等待1秒
6
*/


/////////////////////////////////////////////////////////////////////////////
fun main02() {
    val func = ::getLengthSuspend as (String, Continuation<Int>) -> Any?

    func("Kotlin", object : Continuation<Int> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            println(result.getOrNull())
        }
    })

    // 防止程序提前结束
    Thread.sleep(2000L)
}
/*
输出结果：
等待1秒
6
*/


/////////////////////////////////////////////////////////////////////////////
fun main03() {
    func("Kotlin", object : Callback<Int> {
        override fun resume(result: Int) {
            println(result)
        }
    })

    // 防止程序提前结束
    Thread.sleep(2000L)
}

fun func(text: String, callback: Callback<Int>) {
    thread {
        // 模拟耗时
        Thread.sleep(1000L)
        callback.resume(text.length)
    }
}

interface Callback<T> {
    fun resume(value: T)
}
/*
输出结果：
等待1秒
6
*/


/////////////////////////////////////////////////////////////////////////////
fun main04() = runBlocking {
    val result = testNoSuspendCoroutine()
    println(result)
    Thread.sleep(2000)
}

private suspend fun testNoSuspendCoroutine() = suspendCoroutineUninterceptedOrReturn<String> { continuation ->
    return@suspendCoroutineUninterceptedOrReturn "Hello!"
}

/*
输出结果：
Hello!
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    val result = testSuspendCoroutine()
    println(result)
}

private suspend fun testSuspendCoroutine() = suspendCoroutineUninterceptedOrReturn<String> { continuation ->
    thread {
        Thread.sleep(1000L)
        continuation.resume("Hello!")
    }
    return@suspendCoroutineUninterceptedOrReturn kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
}

/*
输出结果：
等待1秒
Hello!
*/
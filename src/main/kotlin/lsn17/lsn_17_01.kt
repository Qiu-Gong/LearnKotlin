package lsn17

import kotlinx.coroutines.*
import lsn16.logX
import java.util.concurrent.Executors

fun main011() = runBlocking {
    val user = getUserInfo()
    logX(user)
}

suspend fun getUserInfo(): String {
    logX("Before IO Context.")
    withContext(Dispatchers.IO) {
        logX("In IO Context.")
        delay(1000L)
    }
    logX("After IO Context.")
    return "BoyCoder"
}

/*
输出结果：
================================
Before IO Context.
Thread:main @coroutine#1
================================
================================
In IO Context.
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
================================
After IO Context.
Thread:main @coroutine#1
================================
================================
BoyCoder
Thread:main @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
//                          变化在这里
//                             ↓
fun main012() = runBlocking(Dispatchers.IO) {
    val user = getUserInfo()
    logX(user)
}

/*
输出结果：
================================
Before IO Context.
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
================================
In IO Context.
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
================================
After IO Context.
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
================================
BoyCoder
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
//                          变化在这里
//                             ↓
fun main013() = runBlocking(Dispatchers.Default) {
    val user = getUserInfo()
    logX(user)
}

/*
输出结果：
================================
Before IO Context.
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
================================
In IO Context.
Thread:DefaultDispatcher-worker-2 @coroutine#1
================================
================================
After IO Context.
Thread:DefaultDispatcher-worker-2 @coroutine#1
================================
================================
BoyCoder
Thread:DefaultDispatcher-worker-2 @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
val mySingleDispatcher = Executors.newSingleThreadExecutor {
    Thread(it, "MySingleThread").apply { isDaemon = true }
}.asCoroutineDispatcher()

//                         变化在这里
//                            ↓
fun main014() = runBlocking(mySingleDispatcher) {
    val user = getUserInfo()
    logX(user)
}

/*
输出结果：
================================
Before IO Context.
Thread:MySingleThread @coroutine#1
================================
================================
In IO Context.
Thread:DefaultDispatcher-worker-1 @coroutine#1
================================
================================
After IO Context.
Thread:MySingleThread @coroutine#1
================================
================================
BoyCoder
Thread:MySingleThread @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main015() = runBlocking {
    logX("Before launch.") // 1
    launch {
        logX("In launch.") // 2
        delay(1000L)
        logX("End launch.") // 3
    }
    logX("After launch")   // 4
}
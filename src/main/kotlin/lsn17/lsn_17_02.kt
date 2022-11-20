package lsn17

import kotlinx.coroutines.*
import lsn16.logX
import kotlin.coroutines.coroutineContext

/////////////////////////////////////////////////////////////////////////////
fun main021() = runBlocking {
    logX("Before launch.") // 1
    launch {
        logX("In launch.") // 2
        delay(1000L)
        logX("End launch.") // 3
    }
    logX("After launch")   // 4
}

/*
================================
Before launch.
Thread:main @coroutine#1
================================
================================
After launch
Thread:main @coroutine#1
================================
================================
In launch.
Thread:main @coroutine#2
================================
================================
End launch.
Thread:main @coroutine#2
================================
 */


/////////////////////////////////////////////////////////////////////////////
fun main022() = runBlocking {
    logX("Before launch.")  // 1
//               变化在这里
//                  ↓
    launch(Dispatchers.Unconfined) {
        logX("In launch.")  // 2
        delay(1000L)
        logX("End launch.") // 3
    }
    logX("After launch")    // 4
}

/*
输出结果：
================================
Before launch.
Thread:main @coroutine#1
================================
================================
In launch.
Thread:main @coroutine#2
================================
================================
After launch
Thread:main @coroutine#1
================================
================================
End launch.
Thread:kotlinx.coroutines.DefaultExecutor @coroutine#2
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main023() = runBlocking {
    // 仅用于测试，生成环境不要使用这么简易的CoroutineScope
    val scope = CoroutineScope(Job())

    scope.launch {
        logX("First start!")
        delay(1000L)
        logX("First end!") // 不会执行
    }

    scope.launch {
        logX("Second start!")
        delay(1000L)
        logX("Second end!") // 不会执行
    }

    scope.launch {
        logX("Third start!")
        delay(1000L)
        logX("Third end!") // 不会执行
    }

    delay(500L)

    scope.cancel()

    delay(1000L)
}

/*
输出结果：
================================
First start!
Thread:DefaultDispatcher-worker-1 @coroutine#2
================================
================================
Third start!
Thread:DefaultDispatcher-worker-3 @coroutine#4
================================
================================
Second start!
Thread:DefaultDispatcher-worker-2 @coroutine#3
================================
*/


/////////////////////////////////////////////////////////////////////////////
@OptIn(ExperimentalStdlibApi::class)
fun main024() = runBlocking {
    // 注意这里
    val scope = CoroutineScope(Job() + mySingleDispatcher)

    scope.launch {
        // 注意这里
        logX(coroutineContext[CoroutineDispatcher] == mySingleDispatcher)
        delay(1000L)
        logX("First end!")  // 不会执行
    }

    delay(500L)
    scope.cancel()
    delay(1000L)
}
/*
输出结果：
================================
true
Thread:MySingleThread @coroutine#2
================================
*/


/////////////////////////////////////////////////////////////////////////////
@OptIn(ExperimentalStdlibApi::class)
fun main025() = runBlocking {
    val scope = CoroutineScope(Job() + mySingleDispatcher)
    //                                注意这里
    //                                    ↓
    scope.launch(CoroutineName("MyFirstCoroutine!")) {
        logX(coroutineContext[CoroutineDispatcher] == mySingleDispatcher)
        delay(1000L)
        logX("First end!")
    }

    delay(500L)
    scope.cancel()
    delay(1000L)
}

/*
输出结果：

================================
true
Thread:MySingleThread @MyFirstCoroutine!#2  // 注意这里
================================
*/


/////////////////////////////////////////////////////////////////////////////
//  这里使用了挂起函数版本的main()
suspend fun main026() {
    val myExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Catch exception: $throwable")
    }
    val scope = CoroutineScope(Job() + mySingleDispatcher + myExceptionHandler)

    val job = scope.launch() {
        val s: String? = null
        s!!.length // 空指针异常
    }

    job.join()
}
/*
输出结果：
Catch exception: java.lang.NullPointerException
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    println(testContext())
}

suspend fun testContext() {
    coroutineContext
}

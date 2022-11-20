package lsn16

import kotlinx.coroutines.*
import kotlin.random.Random

fun logX(any: Any?) {
    println(
        """
================================
$any 
Thread:${Thread.currentThread().name}
================================""".trimIndent()
    )
}

fun Job.log(tag: String = "") {
    logX(
        """ 
        tag = $tag 
        isActive = $isActive 
        isCancelled = $isCancelled 
        isCompleted = $isCompleted 
        """.trimIndent()
    )
}

fun main011() = runBlocking {
    val job = launch {
        delay(1000L)
    }
    job.log()
    job.cancel()
    job.log()
    delay(1500L)
}


/////////////////////////////////////////////////////////////////////////////
fun main012() = runBlocking {
    //                  变化在这里
    //                      ↓
    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        delay(1000L)
    }
    delay(500L)
    job.log()
    job.start()     // 变化在这里
    job.log()
    delay(500L)
    job.cancel()
    delay(500L)
    job.log()
    delay(2000L)
    logX("Process end!")
}

/*
输出结果：
================================
isActive = false
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
isActive = true
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
Coroutine start!
Thread:main @coroutine#2
================================
================================
isActive = false
isCancelled = true
isCompleted = true
Thread:main @coroutine#1
================================
================================
Process end!
Thread:main @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main013() = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        delay(1000L)
    }
    delay(500L)
    job.log()
    job.start()
    job.log()
    delay(1100L)    // ①
    job.log()
    delay(2000L)    // ②
    logX("Process end!")
}

/*
输出结果：
================================
isActive = false
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
isActive = true
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
Coroutine start!
Thread:main @coroutine#2
================================
================================
isActive = false
isCancelled = false
isCompleted = true
Thread:main @coroutine#1
================================
================================
Process end!
Thread:main @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main014() = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        delay(4000L) // 变化在这里
    }
    delay(500L)
    job.log()
    job.start()
    job.log()
    delay(1100L)
    job.log()
    delay(2000L)
    logX("Process end!")
}

/*
输出结果：
================================
isActive = false
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
isActive = true
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
Coroutine start!
Thread:main @coroutine#2
================================
================================
isActive = true
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
Process end!
Thread:main @coroutine#1
================================
到这里，job仍然还在delay，整个程序并没有完全退出。
*/


/////////////////////////////////////////////////////////////////////////////
fun main015() = runBlocking {
    suspend fun download() {
        // 模拟下载任务
        val time = (Random.nextDouble() * 1000).toLong()
        logX("Delay time: = $time")
        delay(time)
    }

    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        download()
        logX("Coroutine end!")
    }
    delay(500L)
    job.log("1")
    job.start()
    job.log("2")
    job.invokeOnCompletion {
        job.log("3") // 协程结束以后就会调用这里的代码
    }
    job.join()    // 等待协程执行完毕
    logX("Process end!")
}

/*
运行结果：
================================
tag = 1
isActive = false
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
tag = 2
isActive = true
isCancelled = false
isCompleted = false
Thread:main @coroutine#1
================================
================================
Coroutine start!
Thread:main @coroutine#2
================================
================================
Delay time: = 856
Thread:main @coroutine#2
================================
================================
Coroutine end!
Thread:main @coroutine#2
================================
================================
tag = 3
isActive = false
isCancelled = false
isCompleted = true
Thread:main @coroutine#2
================================
================================
Process end!
Thread:main @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    val deferred = async {
        logX("Coroutine start!")
        delay(1000L)
        logX("Coroutine end!")
        "Coroutine result!"
    }
    val result = deferred.await()
    println("Result = $result")
    logX("Process end!")
}

/*
输出结果：
================================
Coroutine start!
Thread:main @coroutine#2
================================
================================
Coroutine end!
Thread:main @coroutine#2
================================
Result = Coroutine result!
================================
Process end!
Thread:main @coroutine#1
================================
*/
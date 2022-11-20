package lsn16

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main021() = runBlocking {
    val parentJob: Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null

    parentJob = launch {
        job1 = launch {
            delay(1000L)
        }

        job2 = launch {
            delay(3000L)
        }

        job3 = launch {
            delay(5000L)
        }
    }

    delay(500L)

    parentJob.children.forEachIndexed { index, job ->
        when (index) {
            0 -> println("job1 === job is ${job1 === job}")
            1 -> println("job2 === job is ${job2 === job}")
            2 -> println("job3 === job is ${job3 === job}")
        }
    }

    parentJob.join() // 这里会挂起大约5秒钟
    logX("Process end!")
}

/*
输出结果：
job1 === job is true
job2 === job is true
job3 === job is true
// 等待大约5秒钟
================================
Process end!
Thread:main @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main022() = runBlocking {
    val parentJob: Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null

    parentJob = launch {
        job1 = launch {
            logX("Job1 start!")
            delay(1000L)
            logX("Job1 done!") // ①，不会执行
        }

        job2 = launch {
            logX("Job2 start!")
            delay(3000L)
            logX("Job2 done!") // ②，不会执行
        }

        job3 = launch {
            logX("Job3 start!")
            delay(5000L)
            logX("Job3 done!")// ③，不会执行
        }
    }

    delay(500L)

    parentJob.children.forEachIndexed { index, job ->
        when (index) {
            0 -> println("job1 === job is ${job1 === job}")
            1 -> println("job2 === job is ${job2 === job}")
            2 -> println("job3 === job is ${job3 === job}")
        }
    }

    parentJob.cancel() // 变化在这里
    logX("Process end!")
}

/*
输出结果：
================================
Job1 start!
Thread:main @coroutine#3
================================
================================
Job2 start!
Thread:main @coroutine#4
================================
================================
Job3 start!
Thread:main @coroutine#5
================================
job1 === job is true
job2 === job is true
job3 === job is true
================================
// 这里不会等待5秒钟
Process end!
Thread:main @coroutine#1
================================
*/


/////////////////////////////////////////////////////////////////////////////
fun main023() = runBlocking {
    suspend fun getResult1(): String {
        delay(1000L) // 模拟耗时操作
        return "Result1"
    }

    suspend fun getResult2(): String {
        delay(1000L) // 模拟耗时操作
        return "Result2"
    }

    suspend fun getResult3(): String {
        delay(1000L) // 模拟耗时操作
        return "Result3"
    }

    val results = mutableListOf<String>()

    val time = measureTimeMillis {
        results.add(getResult1())
        results.add(getResult2())
        results.add(getResult3())
    }
    println("Time: $time")
    println(results)
}

/*
输出结果：
Time: 3012
[Result1, Result2, Result3]
*/


/////////////////////////////////////////////////////////////////////////////
fun main024() = runBlocking {
    suspend fun getResult1(): String {
        delay(1000L) // 模拟耗时操作
        return "Result1"
    }

    suspend fun getResult2(): String {
        delay(1000L) // 模拟耗时操作
        return "Result2"
    }

    suspend fun getResult3(): String {
        delay(1000L) // 模拟耗时操作
        return "Result3"
    }

    val results: List<String>

    val time = measureTimeMillis {
        val result1 = async { getResult1() }
        val result2 = async { getResult2() }
        val result3 = async { getResult3() }

        results = listOf(result1.await(), result2.await(), result3.await())
    }

    println("Time: $time")
    println(results)
}

/*
输出结果：
Time: 1032
[Result1, Result2, Result3]
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    val job = launch {
        logX("First coroutine start!")
        delay(1000L)
        logX("First coroutine end!")
    }

    job.join()
    job.log("1")
    val job2 = launch(job) {// 注意 job 这里
        logX("Second coroutine start!")
        delay(1000L)
        logX("Second coroutine end!")
    }
    job.log("2")
    job2.join()
    job2.log("1")
    logX("Process end!")
}
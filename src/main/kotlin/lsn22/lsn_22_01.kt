package lsn22

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import lsn16.logX
import lsn17.mySingleDispatcher
import kotlin.system.measureTimeMillis


fun main011() = runBlocking {
    var i = 0

    // Default 线程池
    launch(Dispatchers.Default) {
        repeat(1000) {
            i++
        }
    }

    delay(1000L)

    println("i = $i")
}

/*
输出结果
i = 1000
*/


/////////////////////////////////////////////////////////////////////////////
fun main012() = runBlocking {
    var i = 0
    val jobs = mutableListOf<Job>()

    // 重复十次
    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                i++
            }
        }
        jobs.add(job)
    }

    // 等待计算完成
    jobs.joinAll()

    println("i = $i")
}

/*
输出结果
i = 9936
*/


/////////////////////////////////////////////////////////////////////////////
fun main013() = runBlocking {
    var i = 0
    val lock = Any() // 变化在这里

    val jobs = mutableListOf<Job>()

    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                // 变化在这里
                synchronized(lock) {
                    i++
                }
            }
        }
        jobs.add(job)
    }

    jobs.joinAll()

    println("i = $i")
}

/*
输出结果
i = 10000
*/


/////////////////////////////////////////////////////////////////////////////
fun main014() = runBlocking(mySingleDispatcher) {
    suspend fun getResult1(): String {
        logX("Start getResult1")
        delay(1000L) // 模拟耗时操作
        logX("End getResult1")
        return "Result1"
    }

    suspend fun getResult2(): String {
        logX("Start getResult2")
        delay(1000L) // 模拟耗时操作
        logX("End getResult2")
        return "Result2"
    }

    suspend fun getResult3(): String {
        logX("Start getResult3")
        delay(1000L) // 模拟耗时操作
        logX("End getResult3")
        return "Result3"
    }

    logX("main")

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
输出结果
================================
main
Thread:MySingleThread @coroutine#1
================================
================================
Start getResult1
Thread:MySingleThread @coroutine#2
================================
================================
Start getResult2
Thread:MySingleThread @coroutine#3
================================
================================
Start getResult3
Thread:MySingleThread @coroutine#4
================================
================================
End getResult1
Thread:MySingleThread @coroutine#2
================================
================================
End getResult2
Thread:MySingleThread @coroutine#3
================================
================================
End getResult3
Thread:MySingleThread @coroutine#4
================================
Time: 1026
[Result1, Result2, Result3]
*/


/////////////////////////////////////////////////////////////////////////////
fun main015() = runBlocking {
    var i = 0
    val jobs = mutableListOf<Job>()

    repeat(10) {
        val job = launch(mySingleDispatcher) {
            repeat(1000) {
                i++
            }
        }
        jobs.add(job)
    }

    jobs.joinAll()

    println("i = $i")
}

/*
输出结果
i = 10000
*/


/////////////////////////////////////////////////////////////////////////////
fun main016() = runBlocking {
    val mutex = Mutex()

    var i = 0
    val jobs = mutableListOf<Job>()

    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                // 变化在这里
                mutex.lock()
                i++
                mutex.unlock()
            }
        }
        jobs.add(job)
    }

    jobs.joinAll()

    println("i = $i")
}


/////////////////////////////////////////////////////////////////////////////
fun main017() = runBlocking {
    val mutex = Mutex()

    var i = 0
    val jobs = mutableListOf<Job>()

    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                try {
                    mutex.lock()
                    i++
                    i / 0 // 故意制造异常
                    mutex.unlock()
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
        jobs.add(job)
    }

    jobs.joinAll()

    println("i = $i")
}
// 程序无法退出


/////////////////////////////////////////////////////////////////////////////
fun main018() = runBlocking {
    val mutex = Mutex()

    var i = 0
    val jobs = mutableListOf<Job>()

    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                // 变化在这里
                mutex.withLock {
                    i++
                }
            }
        }
        jobs.add(job)
    }

    jobs.joinAll()

    println("i = $i")
}


/////////////////////////////////////////////////////////////////////////////
sealed class Msg
object AddMsg : Msg()
class ResultMsg(val result: CompletableDeferred<Int>) : Msg()

fun main019() = runBlocking {

    suspend fun addActor() = actor<Msg> {
        var counter = 0
        for (msg in channel) {
            when (msg) {
                is AddMsg -> counter++
                is ResultMsg -> msg.result.complete(counter)
            }
        }
    }

    val actor = addActor()
    val jobs = mutableListOf<Job>()

    repeat(10) {
        val job = launch(Dispatchers.Default) {
            repeat(1000) {
                actor.send(AddMsg)
            }
        }
        jobs.add(job)
    }

    jobs.joinAll()

    val deferred = CompletableDeferred<Int>()
    actor.send(ResultMsg(deferred))

    val result = deferred.await()
    actor.close()

    println("i = ${result}")
}


/////////////////////////////////////////////////////////////////////////////
fun main0110() = runBlocking {
    val deferreds = mutableListOf<Deferred<Int>>()

    repeat(10) {
        val deferred = async(Dispatchers.Default) {
            var i = 0
            repeat(1000) {
                i++
            }
            return@async i
        }
        deferreds.add(deferred)
    }

    var result = 0
    deferreds.forEach {
        result += it.await()
    }

    println("i = $result")
}


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    val result = (1..100).map {
        async(Dispatchers.Default) {
            var i = 0
            repeat(1000) {
                i++
            }
            return@async i
        }
    }.awaitAll().sum()
    println("i = $result")
}
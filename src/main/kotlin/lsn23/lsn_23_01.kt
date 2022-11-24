package lsn23

import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun main011() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        while (true) {
            Thread.sleep(500L)
            i++
            println("i = $i")
        }
    }

    delay(2000L)

    job.cancel()
    job.join()

    println("End")
}

/*
输出结果

i = 1
i = 2
i = 3
i = 4
i = 5
// 永远停不下来
*/


/////////////////////////////////////////////////////////////////////////////
fun main012() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        // 变化在这里
        while (isActive) {
            Thread.sleep(500L)
            i++
            println("i = $i")
        }
    }

    delay(2000L)

    job.cancel()
    job.join()

    println("End")
}

/*
输出结果
i = 1
i = 2
i = 3
i = 4
i = 5
End
*/


/////////////////////////////////////////////////////////////////////////////
val fixedDispatcher = Executors.newFixedThreadPool(2) {
    Thread(it, "MyFixedThread").apply { isDaemon = false }
}.asCoroutineDispatcher()

fun main013() = runBlocking {
    // 父协程
    val parentJob = launch(fixedDispatcher) {

        // 1，注意这里 //
        launch(Job()) { // 子协程1
            var i = 0
            while (isActive) {
                Thread.sleep(500L)
                i++
                println("First i = $i")
            }
        }

        launch { // 子协程2
            var i = 0
            while (isActive) {
                Thread.sleep(500L)
                i++
                println("Second i = $i")
            }
        }
    }

    delay(2000L)

    parentJob.cancel()
    parentJob.join()

    println("End")
}

/*
输出结果
First i = 1
Second i = 1
First i = 2
Second i = 2
Second i = 3
First i = 3
First i = 4
Second i = 4
End
First i = 5
First i = 6
// 子协程1永远不会停下来
*/


/////////////////////////////////////////////////////////////////////////////
fun main014() = runBlocking {

    val parentJob = launch(Dispatchers.Default) {
        launch {
            var i = 0
            while (true) {
                // 变化在这里
                delay(500L)
                i++
                println("First i = $i")
            }
        }

        launch {
            var i = 0
            while (true) {
                // 变化在这里
                delay(500L)
                i++
                println("Second i = $i")
            }
        }
    }

    delay(2000L)

    parentJob.cancel()
    parentJob.join()

    println("End")
}

/*
输出结果
First i = 1
Second i = 1
First i = 2
Second i = 2
First i = 3
Second i = 3
End
*/


/////////////////////////////////////////////////////////////////////////////
fun main015() = runBlocking {
    val parentJob = launch(Dispatchers.Default) {
        launch {
            var i = 0
            while (true) {
                // 1
                try {
                    delay(500L)
                } catch (e: CancellationException) {
                    println("Catch CancellationException")
                    // 2
                    throw e // 注释 2：“throw e” 无法停止
                }
                i++
                println("First i = $i")
            }
        }

        launch {
            var i = 0
            while (true) {
                delay(500L)
                i++
                println("Second i = $i")
            }
        }
    }

    delay(2000L)

    parentJob.cancel()
    parentJob.join()

    println("End")
}

/*
输出结果
First i = 1
Second i = 1
First i = 2
Second i = 2
First i = 3
Second i = 3
Second i = 4
Catch CancellationException
End
*/


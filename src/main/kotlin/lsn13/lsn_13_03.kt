import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

// 仅用作研究，工作中别这么写
fun main031() {
    repeat(1000_000_000) {
        thread {
            Thread.sleep(1000000)
        }
    }

    Thread.sleep(10000L)
}

/*
输出结果：
Exception in thread "main" java.lang.OutOfMemoryError: unable to create new native thread
    at java.lang.Thread.start0(Native Method)
    at java.lang.Thread.start(Thread.java:717)
    at kotlin.concurrent.ThreadsKt.thread(Thread.kt:42)
    at kotlin.concurrent.ThreadsKt.thread$default(Thread.kt:20)
*/


/////////////////////////////////////////////////////////////////////////////
// 仅用作研究，工作中别这么写
fun main32() = runBlocking {
    repeat(1000_000_000) {
        launch {
            delay(1000000)
        }
    }

    delay(10000L)
}

/*运行结果：正常*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking(Dispatchers.IO) {
    repeat(3) { first ->
        launch {
            repeat(3) { second ->
                println("repeat first:$first second:$second " + Thread.currentThread().name)
                delay(100)
            }
        }
    }

    delay(5000L)
}

/*
输出结果：
repeat first:0 second:0 DefaultDispatcher-worker-3 @coroutine#2
repeat first:1 second:0 DefaultDispatcher-worker-2 @coroutine#3
repeat first:2 second:0 DefaultDispatcher-worker-4 @coroutine#4
repeat first:1 second:1 DefaultDispatcher-worker-1 @coroutine#3
repeat first:2 second:1 DefaultDispatcher-worker-3 @coroutine#4
repeat first:0 second:1 DefaultDispatcher-worker-4 @coroutine#2
repeat first:1 second:2 DefaultDispatcher-worker-4 @coroutine#3
repeat first:0 second:2 DefaultDispatcher-worker-1 @coroutine#2
repeat first:2 second:2 DefaultDispatcher-worker-3 @coroutine#4
*/
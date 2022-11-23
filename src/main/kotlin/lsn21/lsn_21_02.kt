package lsn21

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

/////////////////////////////////////////////////////////////////////////////
fun main021() = runBlocking {
    val startTime = System.currentTimeMillis()
    val channel1 = produce {
        send(1)
        delay(200L)
        send(2)
        delay(200L)
        send(3)
        delay(150L)
    }

    val channel2 = produce {
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }

    channel1.consumeEach {
        println(it)
    }

    channel2.consumeEach {
        println(it)
    }

    println("Time cost: ${System.currentTimeMillis() - startTime}")
}

/*
输出结果
1
2
3
a
b
c
Time cost: 989
*/


/////////////////////////////////////////////////////////////////////////////
fun main022() = runBlocking {
    val startTime = System.currentTimeMillis()
    val channel1 = produce {
        send("1")
        delay(200L)
        send("2")
        delay(200L)
        send("3")
        delay(150L)
    }

    val channel2 = produce {
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }

    suspend fun selectChannel(
        channel1: ReceiveChannel<String>,
        channel2: ReceiveChannel<String>
    ): String = select<String> {
        // 1， 选择channel1
        channel1.onReceive {
            it.also { println(it) }
        }
        // 2， 选择channel1
        channel2.onReceive {
            it.also { println(it) }
        }
    }

    repeat(6) {// 3， 选择6次结果
        selectChannel(channel1, channel2)
    }

    println("Time cost: ${System.currentTimeMillis() - startTime}")
}

/*
输出结果
1
a
2
b
3
c
Time cost: 540
*/


/////////////////////////////////////////////////////////////////////////////
fun main023() = runBlocking {
    val startTime = System.currentTimeMillis()
    val channel1 = produce<String> {
        // 变化在这里
        delay(15000L)
    }

    val channel2 = produce {
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }

    suspend fun selectChannel(channel1: ReceiveChannel<String>, channel2: ReceiveChannel<String>): String = select<String> {
        channel1.onReceive{
            it.also { println(it) }
        }
        channel2.onReceive{
            it.also { println(it) }
        }
    }

    // 变化在这里
    repeat(3){
        selectChannel(channel1, channel2)
    }

    println("Time cost: ${System.currentTimeMillis() - startTime}")
}

/*
输出结果
a
b
c
Time cost: 533
*/


/////////////////////////////////////////////////////////////////////////////
fun main024() = runBlocking {
    val startTime = System.currentTimeMillis()
    val channel1 = produce<String> {
        // 变化在这里
        delay(15000L)
    }

    val channel2 = produce {
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }

    suspend fun selectChannel(channel1: ReceiveChannel<String>, channel2: ReceiveChannel<String>): String = select<String> {
        channel1.onReceive{
            it.also { println(it) }
        }
        channel2.onReceive{
            it.also { println(it) }
        }
    }

    // 变化在这里
    repeat(6){
        selectChannel(channel1, channel2)
    }

    println("Time cost: ${System.currentTimeMillis() - startTime}")
}

/*
输出结果
a
b
c
Time cost: 533
Exception in thread "main" kotlinx.coroutines.channels.ClosedReceiveChannelException
*/


/////////////////////////////////////////////////////////////////////////////
fun main025() = runBlocking {
    val startTime = System.currentTimeMillis()
    val channel1 = produce<String> {
        delay(15000L)
    }

    val channel2 = produce {
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }

    suspend fun selectChannel(channel1: ReceiveChannel<String>, channel2: ReceiveChannel<String>): String =
        select<String> {
            channel1.onReceiveCatching {
                it.getOrNull() ?: "channel1 is closed!"
            }
            channel2.onReceiveCatching {
                it.getOrNull() ?: "channel2 is closed!"
            }
        }

    repeat(6) {
        val result = selectChannel(channel1, channel2)
        println(result)
    }

    println("Time cost: ${System.currentTimeMillis() - startTime}")
}

/*
输出结果
a
b
c
channel2 is closed!
channel2 is closed!
channel2 is closed!
Time cost: 541
程序不会立即退出
*/


/////////////////////////////////////////////////////////////////////////////
fun main026() = runBlocking {
    val startTime = System.currentTimeMillis()
    val channel1 = produce<String> {
        delay(15000L)
    }

    val channel2 = produce {
        delay(100L)
        send("a")
        delay(200L)
        send("b")
        delay(200L)
        send("c")
    }

    suspend fun selectChannel(channel1: ReceiveChannel<String>, channel2: ReceiveChannel<String>): String =
        select<String> {
            channel1.onReceiveCatching {
                it.getOrNull() ?: "channel1 is closed!"
            }
            channel2.onReceiveCatching {
                it.getOrNull() ?: "channel2 is closed!"
            }
        }

    repeat(6) {
        val result = selectChannel(channel1, channel2)
        println(result)
    }

    // 变化在这里
    channel1.cancel()
    channel2.cancel()

    println("Time cost: ${System.currentTimeMillis() - startTime}")
}


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    suspend fun <T> fastest(vararg deferreds: Deferred<T>): T = select {
        fun cancelAll() = deferreds.forEach { it.cancel() }

        for (deferred in deferreds) {
            deferred.onAwait {
                cancelAll()
                it
            }
        }
    }

    val deferred1 = async {
        delay(100L)
        println("done1")    // 没机会执行
        9.8f
    }

    val deferred2 = async {
        delay(50L)
        println("done2")
        8888
    }

    val deferred3 = async {
        delay(10000L)
        println("done3")    // 没机会执行
        "result3"
    }

    val deferred4 = async {
        delay(2000L)
        println("done4")    // 没机会执行
        1L
    }

    val deferred5 = async {
        delay(14000L)
        println("done5")    // 没机会执行
        Any()
    }

    val result = fastest(deferred1, deferred2, deferred3, deferred4, deferred5)
    println(result)
}

/*
输出结果
done2
result2
*/
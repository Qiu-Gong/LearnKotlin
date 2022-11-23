package lsn21

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
fun main() = runBlocking {
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
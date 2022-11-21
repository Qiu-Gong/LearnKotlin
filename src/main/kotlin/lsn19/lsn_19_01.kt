package lsn19

import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lsn16.logX

fun main011() = runBlocking {
    // 1，创建管道
    val channel = Channel<Int>()

    launch {
        // 2，在一个单独的协程当中发送管道消息
        (1..3).forEach {
            channel.send(it) // 挂起函数
            logX("Send: $it")
        }
    }

    launch {
        // 3，在一个单独的协程当中接收管道消息
        for (i in channel) {  // 挂起函数
            logX("Receive: $i")
        }
    }

    logX("end")
}

/*
输出结果：
================================
end
Thread:main @coroutine#1
================================
================================
Receive: 1
Thread:main @coroutine#3
================================
================================
Send: 1
Thread:main @coroutine#2
================================
================================
Send: 2
Thread:main @coroutine#2
================================
================================
Receive: 2
Thread:main @coroutine#3
================================
================================
Receive: 3
Thread:main @coroutine#3
================================
================================
Send: 3
Thread:main @coroutine#2
================================
// 4，程序不会退出
*/


/////////////////////////////////////////////////////////////////////////////
fun main012() = runBlocking {
    // 1，创建管道
    val channel = Channel<Int>()

    launch {
        // 2，在一个单独的协程当中发送管道消息
        (1..3).forEach {
            channel.send(it) // 挂起函数
            logX("Send: $it")
        }
        channel.cancel()
    }

    launch {
        // 3，在一个单独的协程当中接收管道消息
        for (i in channel) {  // 挂起函数
            logX("Receive: $i")
        }
    }

    logX("end")
}


/////////////////////////////////////////////////////////////////////////////
fun main013() = runBlocking {
    // 变化在这里
    val channel = Channel<Int>(capacity = Channel.Factory.UNLIMITED)
    launch {
        (1..3).forEach {
            channel.send(it)
            println("Send: $it")
        }
        channel.close() // 变化在这里
    }
    launch {
        for (i in channel) {
            println("Receive: $i")
        }
    }
    println("end")
}

/*
输出结果：
end
Send: 1
Send: 2
Send: 3
Receive: 1
Receive: 2
Receive: 3
*/


/////////////////////////////////////////////////////////////////////////////
fun main014() = runBlocking {
    // 变化在这里
    val channel = Channel<Int>(capacity = Channel.Factory.CONFLATED)

    launch {
        (1..3).forEach {
            channel.send(it)
            println("Send: $it")
        }

        channel.close()
    }

    launch {
        for (i in channel) {
            println("Receive: $i")
        }
    }

    println("end")
}

/*
输出结果：
end
Send: 1
Send: 2
Send: 3
Receive: 3
*/


/////////////////////////////////////////////////////////////////////////////
fun main015() = runBlocking {
    // 变化在这里
    val channel = Channel<Int>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    launch {
        (1..3).forEach {
            channel.send(it)
            println("Send: $it")
        }

        channel.close()
    }

    launch {
        for (i in channel) {
            println("Receive: $i")
        }
    }

    println("end")
}

/*
输出结果：
end
Send: 1
Send: 2
Send: 3
Receive: 3
*/


/////////////////////////////////////////////////////////////////////////////
fun main016() = runBlocking {
    // 变化在这里
    val channel = Channel<Int>(
        capacity = 3,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    launch {
        (1..3).forEach {
            channel.send(it)
            println("Send: $it")
        }

        channel.send(4) // 被丢弃
        println("Send: 4")
        channel.send(5) // 被丢弃
        println("Send: 5")

        channel.close()
    }

    launch {
        for (i in channel) {
            println("Receive: $i")
        }
    }

    println("end")
}

/*
输出结果：
end
Send: 1
Send: 2
Send: 3
Send: 4
Send: 5
Receive: 1
Receive: 2
Receive: 3
*/


/////////////////////////////////////////////////////////////////////////////
fun main017() = runBlocking {
    // 无限容量的管道
    val channel = Channel<Int>(Channel.UNLIMITED) {
        println("onUndeliveredElement = $it")
    }

    // 等价这种写法
//    val channel = Channel<Int>(Channel.UNLIMITED, onUndeliveredElement = { println("onUndeliveredElement = $it") })

    // 放入三个数据
    (1..3).forEach {
        channel.send(it)
    }

    // 取出一个，剩下两个
    channel.receive()

    // 取消当前channel
    channel.cancel()
}

/*
输出结果：
onUndeliveredElement = 2
onUndeliveredElement = 3
*/


/////////////////////////////////////////////////////////////////////////////
fun main018() = runBlocking {
    // 变化在这里
    val channel: ReceiveChannel<Int> = produce {
        (1..3).forEach {
            send(it)
            logX("Send: $it")
        }
    }

    launch {
        // 3，接收数据
        for (i in channel) {
            logX("Receive: $i")
        }
    }

    logX("end")
}


/////////////////////////////////////////////////////////////////////////////
fun main019() = runBlocking {
    // 1，创建管道
    val channel: ReceiveChannel<Int> = produce {
        // 发送3条数据
        (1..3).forEach {
            send(it)
        }
    }

    // 调用4次receive()
    channel.receive() // 1
    channel.receive() // 2
    channel.receive() // 3
    channel.tryReceive() // 异常

    logX("end")
}

/*
输出结果：
ClosedReceiveChannelException: Channel was closed
*/


/////////////////////////////////////////////////////////////////////////////
fun main0110() = runBlocking {
    val channel: Channel<Int> = Channel()

    launch {
        (1..3).forEach {
            channel.send(it)
        }
    }

    // 调用4次receive()
    channel.receive()       // 1
    println("Receive: 1")
    channel.receive()       // 2
    println("Receive: 2")
    channel.receive()       // 3
    println("Receive: 3")
    channel.receive()       // 永远挂起

    logX("end")
}

/*
输出结果
Receive: 1
Receive: 2
Receive: 3
*/


/////////////////////////////////////////////////////////////////////////////
fun main0111() = runBlocking {
    // 1，创建管道
    val channel: ReceiveChannel<Int> = produce {
        // 发送3条数据
        (1..3).forEach {
            send(it)
            println("Send $it")
        }
    }

    // 使用while循环判断isClosedForReceive
    while (!channel.isClosedForReceive) {
        val i = channel.receive()
        println("Receive $i")
    }

    println("end")
}

/*
输出结果
Send 1
Receive 1
Receive 2
Send 2
Send 3
Receive 3
end
*/


/////////////////////////////////////////////////////////////////////////////
fun main0112() = runBlocking {
    // 变化在这里
    val channel: ReceiveChannel<Int> = produce(capacity = 3) {
        // 变化在这里
        (1..300).forEach {
            send(it)
            println("Send $it")
        }
    }

    while (!channel.isClosedForReceive) {
        val i = channel.receive()
        println("Receive $i")
    }

    logX("end")
}

/*
输出结果
// 省略部分
Receive 300
Send 300
ClosedReceiveChannelException: Channel was closed
*/


/////////////////////////////////////////////////////////////////////////////
fun main0113() = runBlocking {
    val channel: ReceiveChannel<Int> = produce(capacity = 3) {
        (1..300).forEach {
            send(it)
            println("Send $it")
        }
    }

    // 变化在这里
    channel.consumeEach {
        println("Receive $it")
    }

    logX("end")
}

/*
输出结果：

正常
*/


/////////////////////////////////////////////////////////////////////////////
fun main0114() = runBlocking {
    // 只发送不接受
    val channel = produce<Int>(capacity = 10) {
        (1..3).forEach {
            send(it)
            println("Send $it")
        }
    }

    println("end")
}

/*
输出结果：
end
Send 1
Send 2
Send 3
程序结束
*/


/////////////////////////////////////////////////////////////////////////////
fun main0115() = runBlocking {
    val channel = produce<Int>(capacity = 0) {
        (1..3).forEach {
            println("Before send $it")
            send(it)
            println("Send $it")
        }
    }

    println("end")
}

/*
输出结果：
end
Befour send 1
程序将无法退出
*/


/////////////////////////////////////////////////////////////////////////////
class ChannelModel {
    // 对外只提供读取功能
    val channel: ReceiveChannel<Int> by ::_channel
    private val _channel: Channel<Int> = Channel()

    suspend fun init() {
        (1..3).forEach {
            _channel.send(it)
        }
    }
}

fun main() = runBlocking {
    val model = ChannelModel()
    launch {
        model.init()
    }

    model.channel.consumeEach {
        println(it)
    }
}
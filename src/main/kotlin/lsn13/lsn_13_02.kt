import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

// 代码中一共启动了两个线程
fun main021() {
    println(Thread.currentThread().name)
    thread {
        println(Thread.currentThread().name)
        Thread.sleep(100)
    }
    Thread.sleep(1000L)
}

/*
输出结果：
main
Thread-0
*/


/////////////////////////////////////////////////////////////////////////////
// 代码中一共启动了两个协程
fun main() = runBlocking {
    println(Thread.currentThread().name)

    launch {
        println(Thread.currentThread().name)
        delay(100L)
    }

    Thread.sleep(1000L)
}

/*
输出结果：
main @coroutine#1
main @coroutine#2

这里要配置特殊的VM参数：-Dkotlinx.coroutines.debug
这样一来，Thread.currentThread().name就能会包含：协程的名字@coroutine#1
*/
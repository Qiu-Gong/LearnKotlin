package lsn14

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

fun main011() {
    GlobalScope.launch {
        delay(1000L)
        println("Hello World!")
    }

    Thread.sleep(2000L)
}

/*
输出结果；
Hello World!
*/


/////////////////////////////////////////////////////////////////////////////
fun main012() {
    GlobalScope.launch {
        delay(1000L)
        println("Hello World!")
    }
}

/*
输出结果；
无
*/


/////////////////////////////////////////////////////////////////////////////
fun main013() {
    //              守护线程
    //                 ↓
    thread(isDaemon = true) {
        Thread.sleep(1000L)
        println("Hello World!")
    }
}

/*
输出结果；
无
isDaemon = true 守护线程，当主线程结束后。子线程一起结束
*/


/////////////////////////////////////////////////////////////////////////////
fun main014() {
    GlobalScope.launch {
        println("Coroutine started!")
        delay(1000L)
        println("Hello World!")
    }

    println("Process end!")
}
/*
输出结果；
Process end!
*/


/////////////////////////////////////////////////////////////////////////////
fun main() {
    GlobalScope.launch {// 1
        println("Coroutine started!")    // 2
        delay(1000L)            // 3
        println("Hello World!")          // 4
    }

    println("After launch!")             // 5
    Thread.sleep(2000L)            // 6
    println("Process end!")              // 7
}

/*
协程代码运行顺序是 1、5、6、2、3、4、7

输出结果：
After launch!
Coroutine started!
Hello World!
Process end!
*/
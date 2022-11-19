package lsn14

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main031() {
    runBlocking {      // 1
        println("Coroutine started!")   // 2
        delay(1000L)           // 3
        println("Hello World!")         // 4
    }

    println("After launch!")            // 5
    Thread.sleep(2000L)           // 6
    println("Process end!")             // 7
}

/*
输出结果：
Coroutine started!
Hello World!
After launch!
Process end!
*/


/////////////////////////////////////////////////////////////////////////////
fun main032() {
    runBlocking {
        println("First:${Thread.currentThread().name}")
        delay(1000L)
        println("Hello First!")
    }

    runBlocking {
        println("Second:${Thread.currentThread().name}")
        delay(1000L)
        println("Hello Second!")
    }

    runBlocking {
        println("Third:${Thread.currentThread().name}")
        delay(1000L)
        println("Hello Third!")
    }

    // 删掉了 Thread.sleep
    println("Process end!")
}

/*
输出结果：
First:main @coroutine#1
Hello First!
Second:main @coroutine#2
Hello Second!
Third:main @coroutine#3
Hello Third!
Process end!
*/


/////////////////////////////////////////////////////////////////////////////
fun main() {
    val result = runBlocking {
        delay(1000L)
        // return@runBlocking 可写可不写
        return@runBlocking "Coroutine done!"
    }

    println("Result is: $result")
}
/*
输出结果：
Result is: Coroutine done!
*/
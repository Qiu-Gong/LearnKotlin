import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main041() {
    repeat(3) {
        Thread.sleep(1000L)
        println("Print-1:${Thread.currentThread().name}")
    }

    repeat(3) {
        Thread.sleep(900L)
        println("Print-2:${Thread.currentThread().name}")
    }
}

/*
输出结果：
Print-1:main
Print-1:main
Print-1:main
Print-2:main
Print-2:main
Print-2:main
*/


/////////////////////////////////////////////////////////////////////////////
fun main042() = runBlocking {
    launch {
        repeat(3) {
            delay(1000L)
            println("Print-1:${Thread.currentThread().name}")
        }
    }

    launch {
        repeat(3) {
            delay(900L)
            println("Print-2:${Thread.currentThread().name}")
        }
    }
    delay(3000L)
}

/*
输出结果：
Print-2:main @coroutine#3
Print-1:main @coroutine#2
Print-2:main @coroutine#3
Print-1:main @coroutine#2
Print-2:main @coroutine#3
Print-1:main @coroutine#2

Print-2 和 Print-1 是交替输出的，“coroutine#2”、“coroutine#3”这两个协程是并行的（Concurrent）
执行完后，立马又复用上协程id
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    launch {
        repeat(3) {
            Thread.sleep(1000L)
            println("Print-1:${Thread.currentThread().name}")
        }
    }

    launch {
        repeat(3) {
            Thread.sleep(900L)
            println("Print-2:${Thread.currentThread().name}")
        }
    }
    delay(3000L)
}

/*
    输出结果：
    Print-1:main @coroutine#2
    Print-1:main @coroutine#2
    Print-1:main @coroutine#2
    Print-2:main @coroutine#3
    Print-2:main @coroutine#3
    Print-2:main @coroutine#3
*/
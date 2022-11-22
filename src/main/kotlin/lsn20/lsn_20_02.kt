package lsn20

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main021() = runBlocking {
    val flow = flow {
        emit(1)
        emit(2)
//        throw IllegalStateException()
        emit(3)
    }

    flow.map { it * 2 }
        .catch { println("catch: $it") } // 注意这里
        .collect {
            println(it)
        }
}

/*
输出结果：
2
4
catch: java.lang.IllegalStateException
*/


/////////////////////////////////////////////////////////////////////////////
fun main022() = runBlocking {
    val flow = flow {
        emit(1)
        emit(2)
        emit(3)
    }

    flow.map { it * 2 }
        .catch { println("catch: $it") }
        .filter { it / 0 > 1}  // 故意制造异常
        .collect {
            println(it)
        }
}

/*
输出结果
Exception in thread "main" ArithmeticException: / by zero
*/


/////////////////////////////////////////////////////////////////////////////
fun main023() = runBlocking {
    flowOf(4, 5, 6)
        .onCompletion { println("onCompletion second: $it") }
        .collect {
            try {
                println("collect: $it")
                throw IllegalStateException()
            } catch (e: Exception) {
                println("Catch $e")
            }
        }
}
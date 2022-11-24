package lsn23

import kotlinx.coroutines.*

fun main021() = runBlocking {
    try {
        launch {
            delay(100L)
            1 / 0 // 故意制造异常
        }
    } catch (e: ArithmeticException) {
        println("Catch: $e")
    }

    delay(500L)
    println("End")
}

/*
输出结果：
崩溃
Exception in thread "main" ArithmeticException: / by zero
*/


/////////////////////////////////////////////////////////////////////////////
fun main022() = runBlocking {
    var deferred: Deferred<Unit>? = null
    try {
        deferred = async {
            delay(100L)
            1 / 0
        }
    } catch (e: ArithmeticException) {
        println("Catch: $e")
    }

    deferred?.await()

    delay(500L)
    println("End")
}

/*
输出结果：
崩溃
Exception in thread "main" ArithmeticException: / by zero
*/


/////////////////////////////////////////////////////////////////////////////
fun main023() = runBlocking {
    launch {
        try {
            delay(100L)
            1 / 0 // 故意制造异常
        } catch (e: ArithmeticException) {
            println("Catch: $e")
        }
    }

    delay(500L)
    println("End")
}

/*
输出结果：
Catch: java.lang.ArithmeticException: / by zero
End
*/


/////////////////////////////////////////////////////////////////////////////
fun main024() = runBlocking {
    var deferred: Deferred<Unit>? = null

    deferred = async {
        try {
            delay(100L)
            1 / 0
        } catch (e: ArithmeticException) {
            println("Catch: $e")
        }
    }

    deferred?.await()

    delay(500L)
    println("End")
}


/////////////////////////////////////////////////////////////////////////////
fun main025() = runBlocking {
    val deferred = async {
        delay(100L)
        1 / 0
    }

    try {
        deferred.await()
    } catch (e: ArithmeticException) {
        println("Catch: $e")
    }

    delay(500L)
    println("End")
}

/*
输出结果
Catch: java.lang.ArithmeticException: / by zero
崩溃：
Exception in thread "main" ArithmeticException: / by zero
*/


/////////////////////////////////////////////////////////////////////////////
fun main026() = runBlocking {
    val deferred = async {
        delay(100L)
        1 / 0
    }

    delay(500L)
    println("End")
}

/*
输出结果
崩溃：
Exception in thread "main" ArithmeticException: / by zero
*/


/////////////////////////////////////////////////////////////////////////////
fun main027() = runBlocking {
    val scope = CoroutineScope(SupervisorJob())
    scope.async {
        delay(100L)
        1 / 0
    }

    delay(500L)
    println("End")
}

/*
输出结果
End
*/


/////////////////////////////////////////////////////////////////////////////
fun main028() = runBlocking {
    val scope = CoroutineScope(SupervisorJob())
    // 变化在这里
    val deferred = scope.async {
        delay(100L)
        1 / 0
    }

    try {
        deferred.await()
    } catch (e: ArithmeticException) {
        println("Catch: $e")
    }

    delay(500L)
    println("End")
}

/*
输出结果
Catch: java.lang.ArithmeticException: / by zero
End
*/


/////////////////////////////////////////////////////////////////////////////
fun main029() = runBlocking {

    val scope = CoroutineScope(coroutineContext)
    scope.launch {
        async {
            delay(100L)
        }

        launch {
            delay(100L)

            launch {
                delay(100L)
                1 / 0 // 故意制造异常
            }
        }

        delay(100L)
    }

    delay(1000L)
    println("End")
}

/*
输出结果
Exception in thread "main" ArithmeticException: / by zero
*/


/////////////////////////////////////////////////////////////////////////////
val myExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("Catch exception: $throwable")
}

fun main() = runBlocking {
    // 注意这里
    val scope = CoroutineScope(coroutineContext + Job() + myExceptionHandler)

    scope.launch {
        async {
            delay(100L)
        }

        launch {
            delay(100L)

            launch() {
                delay(100L)
                1 / 0 // 故意制造异常
            }
        }

        delay(100L)
    }

    delay(1000L)
    println("End")
}

/*
Catch exception: ArithmeticException: / by zero
End
*/
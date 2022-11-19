package lsn14

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

// (Int) -> Double
fun func1(num: Int): Double {
    return num.toDouble()
}

val f1: (Int) -> Double = ::func1


/////////////////////////////////////////////////////////////////////////////
// CoroutineScope.(Int) -> Double
fun CoroutineScope.func2(num: Int): Double {
    return num.toDouble()
}

val f2: CoroutineScope.(Int) -> Double = CoroutineScope::func2


/////////////////////////////////////////////////////////////////////////////
// suspend (Int) -> Double
suspend fun func3(num: Int): Double {
    delay(100L)
    return num.toDouble()
}

val f3: suspend (Int) -> Double = ::func3


/////////////////////////////////////////////////////////////////////////////
// suspend CoroutineScope.(Int) -> Double
suspend fun CoroutineScope.func4(num: Int): Double {
    delay(100L)
    return num.toDouble()
}

val f4: suspend CoroutineScope.(Int) -> Double = CoroutineScope::func4

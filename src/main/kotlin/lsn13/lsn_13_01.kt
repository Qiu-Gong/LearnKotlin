import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main011() {
    val list = getList()
    printList(list)
}

fun getList(): List<Int> {
    val list = mutableListOf<Int>()
    println("Add 1")
    list.add(1)
    println("Add 2")
    list.add(2)
    println("Add 3")
    list.add(3)
    println("Add 4")
    list.add(4)

    return list
}

fun printList(list: List<Int>) {
    val i = list[0]
    println("Get$i")
    val j = list[1]
    println("Get$j")
    val k = list[2]
    println("Get$k")
    val m = list[3]
    println("Get$m")
}

/* 运行结果：
Add 1
Add 2
Add 3
Add 4
Get1
Get2
Get3
Get4
*/


/////////////////////////////////////////////////////////////////////////////
fun main012() = runBlocking {
    val sequence = getSequence()
    printSequence(sequence)
}

fun getSequence() = sequence {
    println("Add 1")
    yield(1)
    println("Add 2")
    yield(2)
    println("Add 3")
    yield(3)
    println("Add 4")
    yield(4)
}

fun printSequence(sequence: Sequence<Int>) {
    val iterator = sequence.iterator()
    val i = iterator.next()
    println("Get$i")
    val j = iterator.next()
    println("Get$j")
    val k = iterator.next()
    println("Get$k")
    val m = iterator.next()
    println("Get$m")
}

/*
输出结果：
Add 1
Get1
Add 2
Get2
Add 3
Get3
Add 4
Get4
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    val channel = getProducer(this)
    testConsumer(channel)
}

fun getProducer(scope: CoroutineScope) = scope.produce {
    println("Send 1")
    send(1)
    println("Send 2")
    send(2)
    println("Send 3")
    send(3)
    println("Send 4")
    send(4)
}

suspend fun testConsumer(channel: ReceiveChannel<Int>) {
    delay(100)
    val i = channel.receive()
    println("Receive$i")
    delay(100)
    val j = channel.receive()
    println("Receive$j")
    delay(100)
    val k = channel.receive()
    println("Receive$k")
    delay(100)
    val m = channel.receive()
    println("Receive$m")
}
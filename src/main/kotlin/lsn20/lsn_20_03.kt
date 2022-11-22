package lsn20

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import lsn16.logX
import lsn17.mySingleDispatcher

fun main031() = runBlocking {
    val flow = flow {
        logX("Start")
        emit(1)
        logX("Emit: 1")
        emit(2)
        logX("Emit: 2")
        emit(3)
        logX("Emit: 3")
    }

    flow.filter {
        logX("Filter: $it")
        it > 2
    }.flowOn(Dispatchers.IO)  // 注意这里
        .collect {
            logX("Collect $it")
        }
}

/*
输出结果：
collect{}将运行在main
filter{}运行在DefaultDispatcher
flow{}运行在DefaultDispatcher
*/


/////////////////////////////////////////////////////////////////////////////
fun main032() = runBlocking {
    val flow = flow {
        logX("Start")
        emit(1)
        logX("Emit: 1")
        emit(2)
        logX("Emit: 2")
        emit(3)
        logX("Emit: 3")
    }

    flow.flowOn(Dispatchers.IO) // 注意这里
        .filter {
            logX("Filter: $it")
            it > 2
        }.collect {
            logX("Collect $it")
        }
}

/*
输出结果：
collect{}将运行在main
filter{}运行在main
flow{}运行在DefaultDispatcher
*/


/////////////////////////////////////////////////////////////////////////////
fun main033() = runBlocking {
    val flow = flow {
        logX("Start")
        emit(1)
        logX("Emit: 1")
        emit(2)
        logX("Emit: 2")
        emit(3)
        logX("Emit: 3")
    }

    flow.flowOn(Dispatchers.IO).filter {
        logX("Filter: $it")
        it > 2
    }.collect {
        withContext(mySingleDispatcher) {
            logX("Collect $it")
        }
    }
}

/*
输出结果：
collect{}将运行在MySingleThread
filter{}运行在main
flow{}运行在DefaultDispatcher
*/


/////////////////////////////////////////////////////////////////////////////
fun main034() = runBlocking {
    val flow = flow {
        logX("Start")
        emit(1)
        logX("Emit: 1")
        emit(2)
        logX("Emit: 2")
        emit(3)
        logX("Emit: 3")
    }
    withContext(mySingleDispatcher) {
        flow.flowOn(Dispatchers.IO).filter {
            logX("Filter: $it")
            it > 2
        }.collect {
            logX("Collect $it")
        }
    }
}

/*
输出结果：
collect{}将运行在MySingleThread
filter{}运行在MySingleThread
flow{}运行在DefaultDispatcher
*/


/////////////////////////////////////////////////////////////////////////////
fun main035() = runBlocking {
    val flow = flow {
        logX("Start")
        emit(1)
        logX("Emit: 1")
        emit(2)
        logX("Emit: 2")
        emit(3)
        logX("Emit: 3")
    }

    val scope = CoroutineScope(mySingleDispatcher)
    flow.flowOn(Dispatchers.IO).filter {
        logX("Filter: $it")
        it > 2
    }.onEach {
        logX("onEach $it")
    }.launchIn(scope)

    delay(100L)
}

/*
输出结果：
collect{}将运行在MySingleThread
filter{}运行在MySingleThread
flow{}运行在DefaultDispatcher
*/


/////////////////////////////////////////////////////////////////////////////
fun main036() = runBlocking {
    val scope = CoroutineScope(mySingleDispatcher)

    val flow = flow {
        logX("Start")
        emit(1)
        logX("Emit: 1")
        emit(2)
        logX("Emit: 2")
        emit(3)
        logX("Emit: 3")
    }.flowOn(Dispatchers.IO).filter {
        logX("Filter: $it")
        it > 2
    }.onEach {
        logX("onEach $it")
    }

    scope.launch {
        flow.collect()
    }

    delay(100L)
}


/////////////////////////////////////////////////////////////////////////////
fun main037() = runBlocking {
    // 冷数据流
    val flow = flow {
        (1..3).forEach {
            println("Before send $it")
            emit(it)
            println("Send $it")
        }
    }

    // 热数据流
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
Before send 1
// Flow 当中的代码并未执行
*/


/////////////////////////////////////////////////////////////////////////////
fun main038() = runBlocking {
    fun loadData() = flow {
        repeat(3) {
            delay(100L)
            emit(it)
            logX("emit $it")
        }
    }

    // 模拟Android、Swing的UI
    val uiScope = CoroutineScope(mySingleDispatcher)

    loadData().map {
        logX("map $it")
        it * 2
    }.flowOn(Dispatchers.IO) // 1，耗时任务
        .onEach {
            logX("onEach $it")
        }.launchIn(uiScope)      // 2，UI任务

    delay(1000L)
}

/*
输出结果：
map 运行在 DefaultDispatcher
flow  运行在 DefaultDispatcher
onEach 运行在 mySingleDispatcher
*/


/////////////////////////////////////////////////////////////////////////////
fun main() = runBlocking {
    fun loadData() = flow {
        repeat(3) {
            delay(100L)
            emit(it)
            logX("emit $it")
        }
    }

    fun updateUI(it: Int) {
        logX("updateUI $it")
    }

    fun showLoading() {
        logX("showLoading")
    }

    fun hideLoading(success: Boolean) {
        logX("hideLoading:$success")
    }

    val uiScope = CoroutineScope(mySingleDispatcher)

    loadData()
        .onStart { showLoading() }          // 显示加载弹窗
        .map { it * 2 }
        .flowOn(Dispatchers.IO)
        .catch { throwable ->
            println(throwable)
            hideLoading(false)                   // 隐藏加载弹窗
            emit(-1)                   // 发生异常以后，指定默认值
        }.onEach { updateUI(it) }            // 更新UI界面
        .onCompletion { hideLoading(true) }     // 隐藏加载弹窗
        .launchIn(uiScope)

    delay(5000L)
}

/*
输出结果：
onStart  catch 运行在 DefaultDispatcher
onCompletion 运行在 mySingleDispatcher
map flow  运行在 DefaultDispatcher
updateUI 运行在 mySingleDispatcher
*/

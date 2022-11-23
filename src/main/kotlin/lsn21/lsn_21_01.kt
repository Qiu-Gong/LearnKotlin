package lsn21

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

suspend fun getCacheInfo(productId: String): Product? {
    delay(100L)
    return Product(productId, 9.9)
}

suspend fun getCacheInfo2(productId: String): Product? {
    delay(110L)
    return Product(productId, 9.7)
}

suspend fun getNetworkInfo(productId: String): Product? {
    delay(200L)
    return Product(productId, 9.8)
}

fun updateUI(product: Product) {
    println("${product.productId}==${product.price}")
}

fun main011() = runBlocking {
    val startTime = System.currentTimeMillis()

    val productId = "xxxId"
    // 查询缓存
    val cacheInfo = getCacheInfo(productId)
    if (cacheInfo != null) {
        updateUI(cacheInfo)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }

    // 查询网络
    val latestInfo = getNetworkInfo(productId)
    if (latestInfo != null) {
        updateUI(latestInfo)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }
}

data class Product(
    val productId: String,
    val price: Double,
    // 是不是缓存信息
    val isCache: Boolean = false
)

/*
输出结果
xxxId==9.9
Time cost: 112
xxxId==9.8
Time cost: 314
*/


/////////////////////////////////////////////////////////////////////////////
fun main012() = runBlocking {
    val startTime = System.currentTimeMillis()
    val productId = "xxxId"
    //          1，注意这里
    //               ↓
    val product = select<Product?> {
        // 2，注意这里
        async { getCacheInfo(productId) }
            .onAwait { // 3，注意这里
                it
            }
        // 4，注意这里
        async { getNetworkInfo(productId) }
            .onAwait {  // 5，注意这里
                it
            }
    }

    if (product != null) {
        updateUI(product)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }
}

/*
输出结果
xxxId==9.9
Time cost: 127
*/


/////////////////////////////////////////////////////////////////////////////
fun main013() = runBlocking {

    val startTime = System.currentTimeMillis()
    val productId = "xxxId"

    // 1，缓存和网络，并发执行
    val cacheDeferred = async { getCacheInfo(productId) }
    val latestDeferred = async { getNetworkInfo(productId) }

    // 2，在缓存和网络中间，选择最快的结果
    val product = select<Product?> {
        cacheDeferred.onAwait {
            it?.copy(isCache = true)
        }

        latestDeferred.onAwait {
            it?.copy(isCache = false)
        }
    }

    // 3，更新UI
    if (product != null) {
        updateUI(product)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }

    // 4，如果当前结果是缓存，那么再取最新的网络服务结果
    if (product != null && product.isCache) {
        val latest = latestDeferred.await() ?: return@runBlocking
        updateUI(latest)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }
}

/*
输出结果：
xxxId==9.9
Time cost: 120
xxxId==9.8
Time cost: 220
*/


/////////////////////////////////////////////////////////////////////////////
fun main014() = runBlocking {
    val startTime = System.currentTimeMillis()
    val productId = "xxxId"

    val cacheDeferred = async { getCacheInfo(productId) }
    // 变化在这里
    val cacheDeferred2 = async { getCacheInfo2(productId) }
    val latestDeferred = async { getNetworkInfo(productId) }

    val product = select<Product?> {
        cacheDeferred.onAwait {
            it?.copy(isCache = true)
        }

        // 变化在这里
        cacheDeferred2.onAwait {
            it?.copy(isCache = true)
        }

        latestDeferred.onAwait {
            it?.copy(isCache = false)
        }
    }

    if (product != null) {
        updateUI(product)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }

    if (product != null && product.isCache) {
        val latest = latestDeferred.await() ?: return@runBlocking
        updateUI(latest)
        println("Time cost: ${System.currentTimeMillis() - startTime}")
    }
}

/*
输出结果
xxxId==9.9
Time cost: 125
xxxId==9.8
Time cost: 232
*/
package lsn27

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import lsn16.logX
import kotlin.coroutines.coroutineContext

suspend fun testCoroutine() {
    coroutineContext

    logX("start")
    val user = getUserInfo()
    logX(user)
    val friendList = getFriendList(user)
    logX(friendList)
    val feedList = getFeedList(user, friendList)
    logX(feedList)
}

//挂起函数
// ↓
suspend fun getUserInfo(): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "BoyCoder"
}

//挂起函数
// ↓
suspend fun getFriendList(user: String): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "Tom, Jack"
}

//挂起函数
// ↓
suspend fun getFeedList(user: String, list: String): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "{FeedList..}"
}

suspend fun testContext() = coroutineContext
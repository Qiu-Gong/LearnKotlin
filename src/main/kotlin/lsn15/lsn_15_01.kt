package lsn15

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
    return "$user Tom, Jack"
}

//挂起函数
// ↓
suspend fun getFeedList(list: String): String {
    withContext(Dispatchers.IO) {
        delay(1000L)
    }
    return "$list {FeedList..}"
}

fun main() = runBlocking {
    val user = getUserInfo()
    val friendList = getFriendList(user)
    val feedList = getFeedList(friendList)
    println("result:$feedList")
}

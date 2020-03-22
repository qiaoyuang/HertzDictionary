package com.w10group.hertzdictionary.manager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

internal actual suspend fun readFileToString(fileName: String): List<String> = withContext(Dispatchers.Default) {
    // TODO
    listOf()
}

typealias KV = Pair<String, String>

internal actual suspend fun readFileToKV( fileName: String): List<KV> = withContext(Dispatchers.Default) {
    // TODO
    listOf()
}
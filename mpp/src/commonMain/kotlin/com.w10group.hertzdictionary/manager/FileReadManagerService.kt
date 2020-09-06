package com.w10group.hertzdictionary.manager

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

internal expect suspend fun readFileToString(fileName: String): List<String>

typealias KV = Pair<String, String>

internal expect suspend fun readFileToKV( fileName: String): List<KV>
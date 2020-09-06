package com.w10group.hertzdictionary.core

import com.w10group.hertzdictionary.database.LocalWord
import kotlinx.coroutines.Dispatchers

/**
 * 一些杂项映射
 * @author qiaoyuang
 */

actual val IODispatcher = Dispatchers.Default

actual val SafeListConstructor: () -> MutableList<LocalWord> = { ArrayList() }
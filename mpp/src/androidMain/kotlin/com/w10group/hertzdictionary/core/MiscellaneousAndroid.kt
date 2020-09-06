package com.w10group.hertzdictionary.core

import com.w10group.hertzdictionary.database.LocalWord
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 一些杂项映射
 * @author qiaoyuang
 */

actual val IODispatcher = Dispatchers.IO

actual val SafeListConstructor: () -> MutableList<LocalWord> = { CopyOnWriteArrayList() }
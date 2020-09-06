package com.w10group.hertzdictionary.core

import com.w10group.hertzdictionary.database.LocalWord
import kotlinx.coroutines.CoroutineDispatcher

/**
 * 一些杂项映射
 * @author qiaoyuang
 */

expect val IODispatcher: CoroutineDispatcher

expect val SafeListConstructor: () -> MutableList<LocalWord>
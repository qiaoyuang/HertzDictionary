package com.w10group.hertzdictionary.core

import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * 事件总线
 * 还不完善，缺少缓冲机制以及多线程调度能力
 * 后续计划使用协程实现上述功能
 */

object EventBus {

    val map = HashMap<KClass<*>, LinkedList<out OnWorkListener<*>>>()

    fun <T : Any> put(key: KClass<T>, value: LinkedList<OnWorkListener<T>>) {
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: KClass<T>) = map[key] as LinkedList<OnWorkListener<T>>?

    interface OnWorkListener<in T> {
        fun onWork(event: T)
    }

    inline fun <reified T : Any> register(observer: OnWorkListener<T>) {
        var list = get(T::class)
        if (list == null) {
            list = LinkedList()
            put(T::class, list)
        }
        list.add(observer)
    }

    inline fun <reified T : Any> unRegister(observer: OnWorkListener<T>) {
        val list = get(T::class)
        list?.let { linkedList ->
            linkedList.forEach {
                if (it === observer) {
                    linkedList.remove(it)
                    return
                }
            }
            if (linkedList.isEmpty()) {
                map.remove(T::class)
            }
        }
    }

    inline fun <reified T : Any> post(event: T) = get(T::class)?.forEach { it.onWork(event) }

}
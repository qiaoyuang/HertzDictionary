package com.w10group.hertzdictionary.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * 事件总线
 * @author Qiao
 */

object CoroutinesBus {

    interface OnWorkListener<in T> {
        fun onWork(channel: ReceiveChannel<T>)
    }

    private val map = HashMap<KClass<*>, LinkedList<out Pair<OnWorkListener<*>, Channel<*>>>>()

    private fun <T : Any> put(key: KClass<T>, value: LinkedList<Pair<OnWorkListener<T>, Channel<T>>>) {
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> get(key: KClass<T>) = map[key] as LinkedList<Pair<OnWorkListener<T>, Channel<T>>>?

    fun <T : Any> register(clazz: KClass<T>, observer: OnWorkListener<T>, capacity: Int = 0) {
        var list = get(clazz)
        if (list == null) {
            list = LinkedList()
            put(clazz, list)
        }
        list.add(observer to Channel(capacity))
    }

    inline fun <reified T : Any> register(observer: OnWorkListener<T>,
                                          capacity: Int = 0) = register(T::class, observer, capacity)

    fun <T : Any> unRegister(clazz: KClass<T>, observer: OnWorkListener<T>) {
        val list = get(clazz)
        list?.let { linkedList ->
            linkedList.forEach {
                if (it === observer) {
                    linkedList.remove(it)
                    return
                }
            }
            if (linkedList.isEmpty()) {
                map.remove(clazz)
            }
        }
    }

    inline fun <reified T : Any> unRegister(observer: OnWorkListener<T>) = unRegister(T::class, observer)

    fun <T : Any> CoroutineScope.post(clazz: KClass<T>, event: T) = get(clazz)?.forEach {
        val (onWorkListener, channel) = it
        launch { channel.send(event) }
        onWorkListener.onWork(channel)
    }

    inline fun <reified T : Any> CoroutineScope.post(event: T) = post(T::class, event)

}
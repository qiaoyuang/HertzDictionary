package com.w10group.hertzdictionary.core

import kotlinx.coroutines.channels.Channel
import java.util.LinkedList
import kotlin.reflect.KClass

object CoroutineBus {

    val map = HashMap<KClass<*>, LinkedList<out OnWorkListener<*>>>()

    fun <T : Any> put(key: KClass<T>, value: LinkedList<OnWorkListener<T>>) {
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: KClass<T>): LinkedList<OnWorkListener<T>>?
            = map[key] as LinkedList<OnWorkListener<T>>?

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

    suspend inline fun <reified T : Any> post(event: T) {
       get(T::class)?.let { linkedList ->
           Channel<T>().apply {
               send(event)
               linkedList.forEach {
                   it.onWork(receive())
               }
               close()
           }
       }
    }

}
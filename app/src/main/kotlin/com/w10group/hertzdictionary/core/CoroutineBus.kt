package com.w10group.hertzdictionary.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * 事件总线
 * @author Qiao
 */

object CoroutineBus {

    interface OnWorkListener<in T> {
        fun onWork(t: T)
    }

    private val map = HashMap<KClass<*>, LinkedList<out Pair<OnWorkListener<*>, Channel<*>>>>()

    private fun <T : Any> put(key: KClass<T>, value: LinkedList<Pair<OnWorkListener<T>, Channel<T>>>) {
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> get(key: KClass<T>) = map[key] as LinkedList<Pair<OnWorkListener<T>, Channel<T>>>?

    fun <T : Any> register(clazz: KClass<T>, observer: OnWorkListener<T>, capacity: Int = 0) {
        get(clazz) ?: LinkedList<Pair<OnWorkListener<T>, Channel<T>>>().also { put(clazz, it) }
                .add(observer to Channel(capacity))
    }

    inline fun <reified T : Any> register(observer: OnWorkListener<T>,
                                          capacity: Int = 0) = register(T::class, observer, capacity)

    fun <T : Any> unRegister(clazz: KClass<T>, observer: OnWorkListener<T>) = get(clazz)?.let { list ->
        list.find { it === observer }?.let { list.remove(it) }
        if (list.isEmpty())
            map.remove(clazz)
    }

    inline fun <reified T : Any> unRegister(observer: OnWorkListener<T>) = unRegister(T::class, observer)

     suspend fun <T : Any> post(clazz: KClass<T>, event: T) = coroutineScope {
         get(clazz)?.forEach {
             val (onWorkListener, channel) = it
             launch(Dispatchers.Default) {
                 val deferred = async(Dispatchers.Default) { channel.receive() }
                 channel.send(event)
                 withContext(coroutineContext) {
                     onWorkListener.onWork(deferred.await())
                 }
             }
         }
     }

    suspend inline fun <reified T : Any> post(event: T) = post(T::class, event)

}
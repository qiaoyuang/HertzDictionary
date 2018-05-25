package com.w10group.hertzdictionary.util

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

import kotlin.reflect.KClass


object RxBus {

    const val MAIN = 0
    const val IO = 1
    const val COMPUTATION = 2

    private val map = HashMap<KClass<*>, LinkedList<out OnWorkListener<*>>>()

    fun <T : Any> put(key: KClass<T>, value: LinkedList<OnWorkListener<T>>) {
        map[key] = value
    }

    fun <T : Any> get(key: KClass<T>): LinkedList<OnWorkListener<T>>?
            = map[key] as LinkedList<OnWorkListener<T>>?

    interface OnWorkListener<in T> {
        fun onWork(event: T)
    }

    inline fun <reified T : Any> register(observer: OnWorkListener<T>) {
        var list = get(T :: class)
        if (list == null) {
            list = LinkedList()
            put(T :: class, list)
            list.add(observer)
        } else {
            list.add(observer)
        }
    }

    inline fun <reified T : Any> unRegister(observer: OnWorkListener<T>) {
        val list = get(T :: class)
        list?.let {
            it.forEach {
                if (it === observer) {
                    list.remove(it)
                    return
                }
            }
        }
    }

    inline fun <reified T : Any> post(event: T, observableThread: Int = MAIN, observerThread: Int = MAIN) {
        val observable = Observable.just(event).subscribeOn(intToScheduler(observableThread))
        val list = get(T :: class)
        list?.let {
            it.forEach {
                val observe = it
                observable
                        .observeOn(intToScheduler(observerThread))
                        .subscribe { observe.onWork(it) }
            }
        }
    }

    fun intToScheduler(thread: Int): Scheduler =
            when (thread) {
                MAIN -> AndroidSchedulers.mainThread()
                IO -> Schedulers.io()
                COMPUTATION -> Schedulers.computation()
                else -> AndroidSchedulers.mainThread()
            }

}
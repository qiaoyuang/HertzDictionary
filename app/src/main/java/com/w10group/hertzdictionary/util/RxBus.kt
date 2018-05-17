package com.w10group.hertzdictionary.util

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

import kotlin.reflect.KClass


object RxBus {

    const val MAIN = 0
    const val IO = 1
    const val COMPUTATION = 2

    val map = HashMap<KClass<*>, LinkedList<out OnWorkListener<*>>>()

    interface OnWorkListener<in T> {
        fun onWork(event: T)
    }

    inline fun <reified T> register(observer: OnWorkListener<T>) {
        var list = map[T :: class]
        if (list == null) {
            list = LinkedList<OnWorkListener<T>>()
            map[T :: class] = list
            list.add(observer)
        } else {
            try {
                list as LinkedList<OnWorkListener<T>>
                list.add(observer)
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }

        }
    }

    inline fun <reified T> unRegister(observer: OnWorkListener<T>) {
        val list = map[T :: class]
        list?.let {
            it.forEach {
                if (it === observer) {
                    list.remove(it)
                    return
                }
            }
        }
    }

    @SuppressWarnings
    inline fun <reified T> post(event: T, observableThread: Int = MAIN, observerThread: Int = MAIN) {
        val observable = Observable.just(event).subscribeOn(int2Scheduler(observableThread))
        val list = map[T :: class]
        list?.let {
            it as LinkedList<OnWorkListener<T>>
            it.forEach label@{
                val observe = it
                observable
                        .observeOn(int2Scheduler(observerThread))
                        .subscribe {
                            observe.onWork(it)
                        }
                return@label
            }
        }
    }

    fun int2Scheduler(thread: Int): Scheduler =
            when (thread) {
                MAIN -> AndroidSchedulers.mainThread()
                IO -> Schedulers.io()
                COMPUTATION -> Schedulers.computation()
                else -> AndroidSchedulers.mainThread()
            }

}
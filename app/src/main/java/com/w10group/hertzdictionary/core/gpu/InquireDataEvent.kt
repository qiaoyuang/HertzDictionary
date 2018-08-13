package com.w10group.hertzdictionary.core.gpu

import java.util.*

class InquireDataEvent private constructor(var data: String) {

    companion object {
        private val list by lazy { LinkedList<InquireDataEvent>() }

        fun newInstance(data: String): InquireDataEvent {
            val event = if (list.size < 10) {
                 InquireDataEvent(data)
            } else {
                val element = list.pop()
                element.data = data
                element
            }
            list.push(event)
            return event
        }
    }

}
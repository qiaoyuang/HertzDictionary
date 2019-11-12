package com.w10group.hertzdictionary.core.view

internal external fun valueToCoordinate(index: Int,
                                        time: LongArray,
                                        value: IntArray,
                                        width: Int,
                                        height: Int): FloatArray

internal external fun getTimeTemp(time: LongArray, width: Int, touchX: Float): Int
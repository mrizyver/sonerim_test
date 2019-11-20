package com.izyver.engenering.sonerimtest

import java.util.Comparator

fun <T> binaryPlaceSearch(list: Array<T>, value: T, comparator: Comparator<T>): Int {
    var low = 0
    var height = list.lastIndex
    var mid = 0
    while (low <= height) {
        mid = (low + height) / 2
        val compareResult = comparator.compare(value, list[mid])
        when {
            compareResult < 0 -> height = mid - 1
            compareResult > 0 -> low = mid + 1
            else -> return mid
        }
    }
    return mid
}
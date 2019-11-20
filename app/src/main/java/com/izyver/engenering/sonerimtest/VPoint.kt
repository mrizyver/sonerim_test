package com.izyver.engenering.sonerimtest

import android.graphics.Point

open class VPoint(@JvmField var value: Int, x: Int = 0, y: Int = 0) : Point(x, y){
    override fun equals(other: Any?): Boolean {
        if (other !is VPoint) return false
        return value == other.value && x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        return result
    }
}
package com.engineer.gif.revert.util

import android.graphics.BitmapFactory

/**
 * @author rookie
 * @since 09-24-2019
 */
object BitmapTool {
    fun getBitmapWH(path: String): Pair<Int, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val width = options.outWidth
        val height = options.outHeight
        return Pair(width, height)
    }
}
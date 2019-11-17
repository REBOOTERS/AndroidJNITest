package com.engineer.gif.revert.internal.core

import android.graphics.BitmapFactory
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.IOTool
import com.engineer.gif.revert.lib.AnimatedGIFWriter
import java.io.File
import java.io.FileOutputStream

/**
 * @author rookie
 * @since 11-17-2019
 */
object GenGifFromFramesFastEngine {

    fun genGifByFrames(frames: List<ResFrame>): String {
        val path = IOTool.provideRandomPath("test")
        val os = FileOutputStream(File(path))
        val animatedGIFWriter = AnimatedGIFWriter()
        animatedGIFWriter.prepareForWrite(os, -1, -1)
        for (value in frames) {
            val bitmap = BitmapFactory.decodeFile(value.path)
            animatedGIFWriter.writeFrame(os, bitmap, value.delay)
        }
        animatedGIFWriter.finishWrite(os)
        return path
    }
}
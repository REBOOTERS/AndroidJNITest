package com.engineer.gif.revert.internal.core

import android.content.Context
import android.graphics.BitmapFactory
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.IOTool
import com.engineer.gif.revert.internal.TaskTime
import com.engineer.gif.revert.lib.AnimatedGifEncoder
import java.io.ByteArrayOutputStream

/**
 * @author rookie
 * @since 11-17-2019
 */
object GenGifFromFramesEngine {

    fun genGifByFrames(frames: List<ResFrame>): String {

        val os = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.start(os)
        encoder.setRepeat(0)
        for (value in frames) {
            val bitmap = BitmapFactory.decodeFile(value.path)
            encoder.setDelay(value.delay)
            encoder.addFrame(bitmap)
            bitmap.recycle()
        }
        val t3 = TaskTime()
        encoder.finish()
        t3.release("finish")

        val path = IOTool.saveStreamToSDCard("test", os)
        os.close()

        return path
    }
}
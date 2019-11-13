package com.engineer.gif.revert.internal

import android.content.Context
import android.graphics.BitmapFactory
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.lib.AnimatedGIFWriter
import java.io.File
import java.io.FileOutputStream

/**
 * @author rookie
 * @since 07-06-2019
 */

internal object InnerGifFactory2 : BaseInnerGifFactory() {

    override fun genGifByFrames(context: Context, frames: List<ResFrame>): String {
        val t1 = TaskTime()
        val path = IOTool.provideRandomPath("test")
        val os = FileOutputStream(File(path))
        val animatedGIFWriter = AnimatedGIFWriter()
        animatedGIFWriter.prepareForWrite(os, -1, -1)
        for (value in frames) {
            val bitmap = BitmapFactory.decodeFile(value.path)
            animatedGIFWriter.writeFrame(os, bitmap, value.delay)
        }
        animatedGIFWriter.finishWrite(os)
        t1.release("genGifByFramesWithGPU")
        IOTool.notifySystemGallery(context, path)
        return path
    }


}
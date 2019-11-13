package com.engineer.gif.revert.internal

import android.content.Context
import android.graphics.BitmapFactory
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.lib.AnimatedGifEncoder
import java.io.ByteArrayOutputStream

/**
 * @author rookie
 * @since 07-06-2019
 */


internal object _InnerGifFactory : BaseInnerGifFactory() {

    override fun genGifByFrames(context: Context, frames: List<ResFrame>): String {
        val t1 = TaskTime()

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
        t1.release("genGifByFrames")
        IOTool.notifySystemGallery(context, path)
        log(path)
        return path
    }


}
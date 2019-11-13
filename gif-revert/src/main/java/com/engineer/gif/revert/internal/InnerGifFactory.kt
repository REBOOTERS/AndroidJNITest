package com.engineer.gif.revert.internal

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.lib.AnimatedGifEncoder
import java.io.ByteArrayOutputStream

/**
 * @author rookie
 * @since 07-06-2019
 */
const val TAG = "GifFactory"

internal object _GifFactory :BaseInnerGifFactory(){

    override fun genGifByFrames(context: Context, frames: List<ResFrame>): String {
        val t1 = TaskTime()

        val os = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.start(os)
        encoder.setRepeat(0)
        for (value in frames) {
            val bitmap = BitmapFactory.decodeFile(value.path)
            encoder.setDelay(value.delay)
            val t2 = TaskTime()
            encoder.addFrame(bitmap)
            t2.release("addFrame")
            Log.e("GifFactory", frames.indexOf(value).toString())
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
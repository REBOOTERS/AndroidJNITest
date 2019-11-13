package com.engineer.gif.revert.internal

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.bumptech.glide.gifdecoder.GifDecoder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.FutureTarget
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.lib.AnimatedGIFWriter
import com.engineer.gif.revert.lib.AnimatedGifEncoder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Collections.reverse

/**
 * @author rookie
 * @since 07-06-2019
 */

internal object _GifFactory2 : _BaseGifFactory() {
    
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
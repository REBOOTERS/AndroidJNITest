package com.engineer.gif.revert.internal.core

import android.content.Context
import com.bumptech.glide.gifdecoder.GifDecoder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.FutureTarget
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.IOTool
import com.engineer.gif.revert.internal.ReflectTool
import com.engineer.gif.revert.internal.TaskTime
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author rookie
 * @since 11-17-2019
 */
object GenFramesFromImageEngine {


    fun getFrameResultObservable(context: Context, task: FutureTarget<GifDrawable>): Observable<List<ResFrame>> {

        return Observable.create<List<ResFrame>> {
            val drawable = task.get()
            try {
                val path = supplyFrames(context, drawable)
                it.onNext(path)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())

    }

    fun supplyFrames(context: Context, resource: GifDrawable?): List<ResFrame> {
        if (resource == null) {
            return ArrayList()
        }
        val frames = getResourceFrames(resource, context)
        Collections.reverse(frames)
        return frames
    }

    private fun getResourceFrames(resource: GifDrawable, context: Context): List<ResFrame> {
        val frames = ArrayList<ResFrame>()
        val decoder = getGifDecoder(resource)
        if (decoder != null) {

            for (i in 0..resource.frameCount) {
                val bitmap = decoder.nextFrame
                val path = IOTool.saveBitmap2Box(context, bitmap, "pic_$i")
                val frame = ResFrame(decoder.getDelay(i), path)
                frames.add(frame)
                decoder.advance()
            }
        }
        return frames
    }

    private fun getGifDecoder(resource: GifDrawable): GifDecoder? {
        val t1 = TaskTime()

        var decoder: GifDecoder? = null
        val state = resource.constantState
        if (state != null) {
            val frameLoader = ReflectTool.getAnyByReflect(state, "frameLoader")
            if (frameLoader != null) {
                val any = ReflectTool.getAnyByReflect(frameLoader, "gifDecoder")
                if (any is GifDecoder) {
                    decoder = any
                }
            }
        }
        t1.release("getGifDecoder")
        return decoder
    }
}
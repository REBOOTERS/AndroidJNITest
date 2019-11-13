package com.engineer.gif.revert.internal

import android.content.Context
import android.util.Log
import com.bumptech.glide.gifdecoder.GifDecoder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.FutureTarget
import com.engineer.gif.revert.ResFrame
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author rookie
 * @since 11-12-2019
 */
abstract class BaseInnerGifFactory {
    fun getTaskResult(context: Context, task: FutureTarget<GifDrawable>): Observable<String> {

        return Observable.create<String> {
            val drawable = task.get()
            try {
                val path = reverseRes(context, drawable)
                it.onNext(path)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }

    private fun reverseRes(context: Context, resource: GifDrawable?): String {
        if (resource == null) {
            return ""
        }
        val frames = getResourceFrames(resource, context)


        Collections.reverse(frames)

        return genGifByFrames(context, frames)
    }


    private fun getResourceFrames(resource: GifDrawable, context: Context): List<ResFrame> {
        val t1 = TaskTime()
        val frames = ArrayList<ResFrame>()
        val decoder = getGifDecoder(resource)
        if (decoder != null) {

            for (i in 0..resource.frameCount) {
                val bitmap = decoder.nextFrame
                val path = IOTool.saveBitmap2Box(context, bitmap, "pic_$i")
//                log(path)
                val frame = ResFrame(decoder.getDelay(i), path)
                frames.add(frame)
                decoder.advance()
            }
        }
        t1.release("getResourceFrames")
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


    fun getFrameResult(context: Context, task: FutureTarget<GifDrawable>): Observable<List<ResFrame>> {

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

    private fun supplyFrames(context: Context, resource: GifDrawable?): List<ResFrame> {
        if (resource == null) {
            return ArrayList()
        }
        val frames = getResourceFrames(resource, context)
        Collections.reverse(frames)
        return frames
    }

    abstract fun genGifByFrames(context: Context, frames: List<ResFrame>): String


    protected fun log(msg: String) {
        Log.e(TAG, msg)
    }
}
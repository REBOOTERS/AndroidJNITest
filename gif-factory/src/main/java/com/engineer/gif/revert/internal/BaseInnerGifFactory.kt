package com.engineer.gif.revert.internal

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.FutureTarget
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.core.GenFramesFromImageEngine
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author rookie
 * @since 11-12-2019
 */
const val TAG = "GifFactory"

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
        val frames = GenFramesFromImageEngine.supplyFrames(context, resource)
        return genGifByFrames(context, frames)
    }


    abstract fun genGifByFrames(context: Context, frames: List<ResFrame>): String


    protected fun log(msg: String) {
        Log.e(TAG, msg)
    }
}
package com.engineer.gif

import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.core.GenGifFromFramesEngine
import com.engineer.gif.revert.internal.core.GenGifFromFramesFastEngine
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author zhuyongging @ Zhihu Inc.
 * @since 11-17-2019
 *
 * 通过一些图片生成 gif 工厂
 */
object GifGenFactory {


    fun genGifFromFile(frames: List<ResFrame>): Observable<String> {
        val path = GenGifFromFramesEngine.genGifByFrames(frames)
        return Observable
                .just(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    }


    fun genGifFastModeFromFile(frames: List<ResFrame>): Observable<String> {

        return Observable.create<String> {
            val path = GenGifFromFramesFastEngine.genGifByFrames(frames)
            it.onNext(path)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    }
}
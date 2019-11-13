package com.engineer.gif.revert

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.engineer.gif.revert.internal.GlideInternal
import com.engineer.gif.revert.internal._InnerGifFactory
import io.reactivex.Observable
import org.jetbrains.annotations.Nullable
import java.io.File


/**
 *  根据 gif 图提供 frames 序列
 */
object FramesFactory {

    fun getReverseFrames(context: Context, @RawRes @DrawableRes @Nullable resourceId: Int?): Observable<List<ResFrame>> {
        return _InnerGifFactory.getFrameResult(context, GlideInternal.load(context, resourceId))
    }

    fun getReverseFrames(context: Context, @Nullable file: File?): Observable<List<ResFrame>> {
        return _InnerGifFactory.getFrameResult(context, GlideInternal.load(context, file))
    }

    fun getReverseFrames(context: Context, @Nullable uri: Uri?): Observable<List<ResFrame>> {
        return _InnerGifFactory.getFrameResult(context, GlideInternal.load(context, uri))
    }

    fun getReverseFrames(context: Context, url: String?): Observable<List<ResFrame>> {
        if (TextUtils.isEmpty(url)) {
            return Observable.just(ArrayList())
        }
        val futureTask = GlideInternal.load(context, url)
        return _InnerGifFactory.getFrameResult(context, futureTask)
    }
}
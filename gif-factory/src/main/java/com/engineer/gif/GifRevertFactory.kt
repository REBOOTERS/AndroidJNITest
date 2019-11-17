package com.engineer.gif

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.engineer.gif.revert.internal.GlideInternal
import com.engineer.gif.revert.internal.InnerGifFastFactory
import com.engineer.gif.revert.internal._InnerGifFactory
import io.reactivex.Observable
import org.jetbrains.annotations.Nullable
import java.io.File

/**
 * @author rookie
 * @since 07-06-2019
 *
 * Gif 倒序工厂
 */


object GifRevertFactory {


    fun getReverseRes(context: Context, @RawRes @DrawableRes @Nullable resourceId: Int?): Observable<String> {
        return _InnerGifFactory.getTaskResult(context, GlideInternal.load(context, resourceId))
    }

    fun getReverseRes(context: Context, @Nullable file: File?): Observable<String> {
        return _InnerGifFactory.getTaskResult(context, GlideInternal.load(context, file))
    }

    fun getReverseRes(context: Context, @Nullable uri: Uri?): Observable<String> {
        return _InnerGifFactory.getTaskResult(context, GlideInternal.load(context, uri))
    }

    fun getReverseRes(context: Context, url: String?): Observable<String> {
        if (TextUtils.isEmpty(url)) {
            return Observable.just("")
        }
        val futureTask = GlideInternal.load(context, url)
        return _InnerGifFactory.getTaskResult(context, futureTask)
    }

    /**
     * 更快的实现
     */

    fun getReverseResFast(context: Context, @RawRes @DrawableRes @Nullable resourceId: Int?): Observable<String> {
        return InnerGifFastFactory.getTaskResult(context, GlideInternal.load(context, resourceId))
    }

    fun getReverseResFast(context: Context, @Nullable file: File?): Observable<String> {
        return InnerGifFastFactory.getTaskResult(context, GlideInternal.load(context, file))
    }

    fun getReverseResFast(context: Context, @Nullable uri: Uri?): Observable<String> {
        return InnerGifFastFactory.getTaskResult(context, GlideInternal.load(context, uri))
    }

    fun getReverseResFast(context: Context, url: String?): Observable<String> {
        if (TextUtils.isEmpty(url)) {
            return Observable.just("")
        }
        val futureTask = GlideInternal.load(context, url)
        return InnerGifFastFactory.getTaskResult(context, futureTask)
    }
}
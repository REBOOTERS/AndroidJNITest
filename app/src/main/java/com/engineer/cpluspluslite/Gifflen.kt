/**
 * Copyright 2017 lchad
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.engineer.cpluspluslite


import android.content.Context
import android.content.res.TypedArray
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class Gifflen private constructor(private val mColor: Int,
                                  private val mQuality: Int,
                                  private val mDelay: Int,
                                  private val mWidth: Int,
                                  private val mHeight: Int,
                                  private val mOnEncodeFinishListener: OnEncodeFinishListener?) {

    private var mTargetPath: String? = null

    private val mHandler: Handler

    init {
        mHandler = Handler(Looper.getMainLooper())
    }

    /**
     * 返回一个Builder对象.
     *
     * @return
     */
    fun newBuilder(): Builder {
        return Builder()
    }

    /**
     * Gifflen addFrame
     *
     * @param pixels pixels array from bitmap
     * @return 是否成功.
     */
    private external fun addFrame(pixels: IntArray): Int

    /**
     * Gifflen init
     *
     * @param path    Gif 图片的保存路径
     * @param width   Gif 图片的宽度.
     * @param height  Gif 图片的高度.
     * @param color   Gif 图片的色域.
     * @param quality 进行色彩量化时的quality参数.
     * @param delay   相邻的两帧之间的时间间隔.
     * @return 如果返回值不是0, 就代表着执行失败.
     */
    private external fun init(path: String, width: Int, height: Int, color: Int, quality: Int, delay: Int): Int

    /**
     * * native层做一些释放资源的操作.
     */
    private external fun close()

    /**
     * 开始进行Gif生成
     *
     * @param width  宽度
     * @param height 高度
     * @param path   Gif保存的路径
     * @param files  传入的每一帧图片的File对象
     * @return 是否成功
     */
    fun encode(width: Int, height: Int, path: String, files: List<File>): Boolean {
        check(width, height, path)
        val state: Int
        val pixels = IntArray(width * height)

        state = init(path, width, height, mColor, mQuality, mDelay / 10)
        if (state != 0) {
            // 失败
            return false
        }

        for (aFileList in files) {
            var bitmap: Bitmap
            try {
                bitmap = BitmapFactory.decodeStream(FileInputStream(aFileList))
            } catch (e: FileNotFoundException) {
                return false
            }

            if (width < bitmap.width || height < bitmap.height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            addFrame(pixels)
            bitmap.recycle()
        }

        close()

        return true
    }

    /**
     * 开始进行Gif生成
     *
     * @param path  Gif保存的路径
     * @param files 传入的每一帧图片的File对象
     * @return 是否成功
     */
    fun encode(path: String, files: List<File>): Boolean {
        return encode(mWidth, mHeight, path, files)
    }

    /**
     * 开始进行Gif生成
     *
     * @param context      上下文对象.
     * @param path         Gif保存的路径.
     * @param width        宽度.
     * @param height       高度.
     * @param drawableList 传入的图片资源id数组.
     * @return 是否成功.
     */
    fun encode(context: Context, path: String, width: Int, height: Int, drawableList: IntArray?): Boolean {
        check(width, height, path)
        if (drawableList == null || drawableList.size == 0) {
            return false
        }
        val state: Int
        val pixels = IntArray(width * height)

        state = init(path, width, height, mColor, mQuality, mDelay / 10)
        if (state != 0) {
            // 失败
            return false
        }

        for (drawable in drawableList) {
            var bitmap: Bitmap
            bitmap = BitmapFactory.decodeResource(context.resources, drawable)
            if (width < bitmap.width || height < bitmap.height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            addFrame(pixels)
            bitmap.recycle()
        }
        close()

        return true
    }

    /**
     * 开始进行Gif生成
     *
     * @param context      上下文对象.
     * @param path         Gif保存的路径.
     * @param drawableList 传入的图片资源id数组.
     * @return 是否成功.
     */
    fun encode(context: Context, path: String, drawableList: IntArray): Boolean {
        return encode(context, path, mWidth, mHeight, drawableList)
    }

    /**
     * 开始进行Gif生成
     *
     * @param context 上下文对象.
     * @param path    Gif保存的路径.
     * @param width   宽度.
     * @param height  高度.
     * @param uriList 传入的Uri数组.
     * @return 是否成功.
     */
    fun encode(context: Context, path: String, width: Int, height: Int, uriList: List<Uri>?): Boolean {
        check(width, height, path)
        if (uriList == null || uriList.size == 0) {
            return false
        }
        val state: Int
        val pixels = IntArray(width * height)

        state = init(path, width, height, mColor, mQuality, mDelay / 10)
        if (state != 0) {
            // Failed
            return false
        }

        for (uri in uriList) {
            var bitmap: Bitmap
            val sourcePath = getRealPathFromURI(context, uri)
            if (TextUtils.isEmpty(sourcePath)) {
                Log.e(TAG, "the file path from url is empty")
                continue
            }
            bitmap = BitmapFactory.decodeFile(sourcePath)
            if (width < bitmap.width || height < bitmap.height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            addFrame(pixels)
            bitmap.recycle()
        }
        close()

        return true
    }

    /**
     * 开始进行Gif生成
     *
     * @param context 上下文对象.
     * @param path    Gif保存的路径.
     * @param uriList 传入的Uri数组.
     * @return 是否成功.
     */
    fun encode(context: Context, path: String, uriList: List<Uri>): Boolean {
        return encode(context, path, mWidth, mHeight, uriList)
    }

    /**
     * 从Uri获取图片的绝对路径
     *
     * @param context    上下文对象.
     * @param contentUri 传入的Uri数组.
     * @return 文件绝对路径.
     */
    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, projection, null, null, null)
            if (cursor != null) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(column_index)
            }

        } finally {
            cursor?.close()
        }
        return ""
    }


    /**
     * 开始进行Gif生成
     *
     * @param path       Gif保存的路径.
     * @param width      宽度.
     * @param height     高度.
     * @param bitmapList 传入的Bitmap数组.
     * @return 是否成功.
     */
    fun encode(path: String, width: Int, height: Int, bitmapList: Array<Bitmap>?): Boolean {
        check(width, height, path)
        if (bitmapList == null || bitmapList.size == 0) {
            return false
        }
        val state: Int
        val pixels = IntArray(width * height)

        state = init(path, width, height, mColor, mQuality, mDelay / 10)
        if (state != 0) {
            // 失败
            return false
        }

        for (bitmap in bitmapList) {
            var result: Bitmap? = null
            if (width < bitmap.width || height < bitmap.height) {
                result = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
            result?.getPixels(pixels, 0, width, 0, 0, width, height)
            addFrame(pixels)
            result?.recycle()
        }
        close()

        return true
    }

    /**
     * 开始进行Gif生成
     *
     * @param path       Gif保存的路径.
     * @param bitmapList 传入的Bitmap数组.
     * @return 是否成功.
     */
    fun encode(path: String, bitmapList: Array<Bitmap>): Boolean {
        return encode(path, mWidth, mHeight, bitmapList)
    }

    /**
     * 开始进行Gif生成
     *
     * @param context    上下文对象.
     * @param path       Gif保存的路径.
     * @param width      宽度.
     * @param height     高度.
     * @param typedArray Android资源数组对象.
     * @return 是否成功.
     */
    fun encode(context: Context, path: String, width: Int, height: Int, typedArray: TypedArray?): Boolean {
        check(width, height, path)
        if (typedArray == null || typedArray.length() == 0) {
            return false
        }
        val state: Int
        val pixels = IntArray(width * height)

        state = init(path, width, height, mColor, mQuality, mDelay / 10)
        if (state != 0) {
            // 失败
            return false
        }

        for (i in 0 until typedArray.length()) {
            var bitmap: Bitmap
            bitmap = BitmapFactory.decodeResource(context.resources, typedArray.getResourceId(i, -1))
            if (width < bitmap.width || height < bitmap.height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            addFrame(pixels)
            bitmap.recycle()
        }
        close()

        return true
    }

    /**
     * 开始进行Gif生成
     *
     * @param context    上下文对象.
     * @param path       Gif保存的路径.
     * @param typedArray Android资源数组对象.
     * @return 是否成功.
     */
    fun encode(context: Context, path: String, typedArray: TypedArray): Boolean {
        return encode(context, path, mWidth, mHeight, typedArray)
    }

    class Builder {

        private var color: Int = 0
        private var quality: Int = 0
        private var delay: Int = 0
        private var width: Int = 0
        private var height: Int = 0

        private var onEncodeFinishListener: OnEncodeFinishListener? = null

        init {
            color = DEFAULT_COLOR
            quality = DEFAULT_QUALITY
            delay = DEFAULT_DELAY
            width = DEFAULT_WIDTH
            height = DEFAULT_HEIGHT
        }

        fun color(color: Int): Builder {
            this.color = color
            return this
        }

        fun quality(quality: Int): Builder {
            this.quality = quality
            return this
        }

        fun delay(delay: Int): Builder {
            this.delay = delay
            return this
        }

        fun width(wdith: Int): Builder {
            this.width = wdith
            return this
        }

        fun height(height: Int): Builder {
            this.height = height
            return this
        }

        fun listener(onEncodeFinishListener: OnEncodeFinishListener): Builder {
            this.onEncodeFinishListener = onEncodeFinishListener
            return this
        }

        fun build(): Gifflen {
            if (this.color < 2 || this.color > 256) {
                this.color = DEFAULT_COLOR
            }
            if (this.quality <= 0 || this.quality > 100) {
                quality = DEFAULT_QUALITY
            }

            check(this.delay > 0) { "the delay time value is invalid!!" }
            check(this.width > 0) { "the width value is invalid!!" }
            check(this.height > 0) { "the height value is invalid!!" }
            return Gifflen(this.color, this.quality, this.delay, width, height, onEncodeFinishListener)
        }
    }

    private fun check(width: Int, height: Int, targetPath: String?) {
        if (targetPath != null && targetPath.length > 0) {
            mTargetPath = targetPath
        } else {
            throw IllegalStateException("the target path is invalid!!")
        }
        check(!(width <= 0 || height <= 0)) { "the width or height value is invalid!!" }
    }

    fun onEncodeFinish() {
        if (mOnEncodeFinishListener != null) {
            mHandler.post { mOnEncodeFinishListener.onEncodeFinish(mTargetPath) }
        }
    }

    @FunctionalInterface
    interface OnEncodeFinishListener {
        fun onEncodeFinish(path: String?)
    }

    companion object {

        private val TAG = "Gifflen"

        init {
            System.loadLibrary("gifflen")
        }

        private val DEFAULT_COLOR = 256
        private val DEFAULT_QUALITY = 10
        private val DEFAULT_WIDTH = 320
        private val DEFAULT_HEIGHT = 320
        private val DEFAULT_DELAY = 500
    }

}

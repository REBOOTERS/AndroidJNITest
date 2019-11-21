package com.engineer.gif.video

import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * 视频转换为帧序列
 */
class VideoToFrames {
    private var scaleX = DEFAULT_SCALE
    private var scaleY = DEFAULT_SCALE
    private var quality = DEFAULT_QUALITY
    /**
     * GIF输出路径
     */
    private var outputPath: String
    /**
     * 进度监听回调
     */
    private var onTransformProgressListener: OnTransformProgressListener? = null

    @JvmOverloads
    constructor(outputPath: String, scaleX: Int = DEFAULT_SCALE, scaleY: Int = DEFAULT_SCALE) {
        require(!(scaleX < 1 || scaleY < 1)) { "The zoom level needs to be greater than 1!" }
        require(!TextUtils.isEmpty(outputPath)) { "Save path cannot be empty!" }
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.outputPath = outputPath
    }

    constructor(outputPath: String, scaleX: Int, scaleY: Int, onTransformProgressListener: OnTransformProgressListener?) {
        require(!(scaleX < 1 || scaleY < 1)) { "The zoom level needs to be greater than 1!" }
        require(!TextUtils.isEmpty(outputPath)) { "Save path cannot be empty!" }
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.outputPath = outputPath
        this.onTransformProgressListener = onTransformProgressListener
    }


    fun genFramesformFromVideo(videoPath: String?, startMillSecond: Long, endMillSecond: Long, periodMillSecond: Long): ArrayList<Bitmap>? {
        require(!TextUtils.isEmpty(videoPath)) { "VideoPath is empty!" }
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
        } catch (e: Exception) {
            e.printStackTrace()
            val inputStream: FileInputStream
            try {
                inputStream = FileInputStream(File(videoPath).absolutePath)
                retriever.setDataSource(inputStream.fd)
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return transformWithMediaMetadataRetriever(retriever, startMillSecond, endMillSecond, periodMillSecond)
    }


    fun genFramesformFromVideo(uri: Uri?, startMillSecond: Long, endMillSecond: Long, periodMillSecond: Long): ArrayList<Bitmap>? {
        require(!(uri == null || TextUtils.isEmpty(uri.path))) { "uri is empty!" }
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(uri.path)
        } catch (e: Exception) {
            e.printStackTrace()
            val inputStream: FileInputStream
            try {
                inputStream = FileInputStream(File(uri.path).absolutePath)
                retriever.setDataSource(inputStream.fd)
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return transformWithMediaMetadataRetriever(retriever, startMillSecond, endMillSecond, periodMillSecond)
    }

    /**
     * 从资源文件的Video转换
     *
     * @param afd              AssetFileDescriptor
     * @param startMillSecond  开始转换的起始时间
     * @param endMillSecond    转换的结束时间
     * @param periodMillSecond 转换周期
     * @return 转换是否完成
     */
    fun genFramesformFromVideo(afd: AssetFileDescriptor?, startMillSecond: Long, endMillSecond: Long, periodMillSecond: Long): ArrayList<Bitmap>? {
        requireNotNull(afd) { "AssetFileDescriptor is empty!" }
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return transformWithMediaMetadataRetriever(retriever, startMillSecond, endMillSecond, periodMillSecond)
    }

    /**
     * 从Video转换-真是转换
     *
     * @param retriever        MediaMetadataRetriever
     * @param startMillSecond  开始转换的起始时间
     * @param endMillSecond    转换的结束时间
     * @param periodMillSecond 转换周期
     * @return 转换是否完成
     */
    private fun transformWithMediaMetadataRetriever(retriever: MediaMetadataRetriever, startMillSecond: Long, endMillSecond: Long, periodMillSecond: Long): ArrayList<Bitmap>? {
        require(!(startMillSecond < 0 || endMillSecond <= 0 || startMillSecond >= endMillSecond || periodMillSecond <= 0)) { "startMillSecond and endMillSecond must > 0 , startMillSecond >= endMillSecond" }
        return try {
            val bitmaps: ArrayList<Bitmap> = ArrayList()
            // 获取视频时长
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = durationStr.toLong()
            // 获取最短时间
            val endTime = Math.min(duration, endMillSecond)
            // 开始到结束时间循环遍历 periodMillSecond为一个周期
            var mill = startMillSecond
            while (mill < endTime) {
                bitmaps.add(retriever.getFrameAtTime(mill * 1000, MediaMetadataRetriever.OPTION_CLOSEST))
                mill += periodMillSecond
            }
            retriever.release()
            //            return transform(bitmaps);
            return bitmaps
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun setQuality(quality: Int) {
        this.quality = quality
    }

    fun setScaleX(scaleX: Int) {
        this.scaleX = scaleX
    }

    fun setScaleY(scaleY: Int) {
        this.scaleY = scaleY
    }

    fun setOnTransformProgressListener(onTransformProgressListener: OnTransformProgressListener?) {
        this.onTransformProgressListener = onTransformProgressListener
    }

    companion object {
        /**
         * 缩放程度
         */
        private const val DEFAULT_SCALE = 1
        /**
         * GIF质量
         */
        private const val DEFAULT_QUALITY = 100
    }
}
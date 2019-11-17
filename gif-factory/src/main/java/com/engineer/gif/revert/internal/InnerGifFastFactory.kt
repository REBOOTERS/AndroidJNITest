package com.engineer.gif.revert.internal

import android.content.Context
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.core.GenGifFromFramesEngine
import com.engineer.gif.revert.internal.core.GenGifFromFramesFastEngine

/**
 * @author rookie
 * @since 07-06-2019
 */

internal object InnerGifFastFactory : BaseInnerGifFactory() {

    override fun genGifByFrames(context: Context, frames: List<ResFrame>): String {
        val t1 = TaskTime()
        val path = GenGifFromFramesFastEngine.genGifByFrames(frames)
        t1.release("InnerGifFastFactory")
        IOTool.notifySystemGallery(context, path)
        return path
    }
}
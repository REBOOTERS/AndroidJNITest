package com.engineer.gif.revert.internal

import android.content.Context
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.revert.internal.core.GenGifFromFramesEngine

/**
 * @author rookie
 * @since 07-06-2019
 */


internal object _InnerGifFactory : BaseInnerGifFactory() {

    override fun genGifByFrames(context: Context, frames: List<ResFrame>): String {
        val t1 = TaskTime()
        val path = GenGifFromFramesEngine.genGifByFrames(frames)
        t1.release("genGifByFrames")
        IOTool.notifySystemGallery(context, path)
        return path
    }


}
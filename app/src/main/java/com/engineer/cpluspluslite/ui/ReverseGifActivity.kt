package com.engineer.cpluspluslite.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.engineer.cpluspluslite.Gifflen
import com.engineer.cpluspluslite.R
import com.engineer.gif.GifRevertFactory
import com.engineer.gif.revert.FramesFactory
import com.engineer.gif.revert.util.BitmapTool
import com.wang.avi.indicators.BallSpinFadeLoaderIndicator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_reverse_gif.*
import kotlinx.android.synthetic.main.image_container.*
import java.io.File

class ReverseGifActivity : BaseActivity() {

    // <editor-fold defaultstate="collapsed" desc="onCreate">


    private var useNative = false
    private var type = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_gif)
        start.setOnClickListener { selectGif(false, 1) }
        start2.setOnClickListener { selectGif(false, 0) }
        go.setOnClickListener { selectGif(true, 0) }

        share.setOnClickListener {
            activityDelegate.share(originalUrl, revertedlUrl)
        }
        file.setOnClickListener {
            activityDelegate.openFileSystem()
        }

        Glide.with(this).load(R.drawable.haha).into(original)
        Glide.with(this).load(R.drawable.haha_revert).into(reversed)
        av.indicator = BallSpinFadeLoaderIndicator()
    }
    // </editor-fold>

    @SuppressLint("CheckResult")
    private fun selectGif(useNative: Boolean, i: Int) {
        if (!mHasPermission) {
            return
        }
        this.useNative = useNative
        this.type = i
        activityDelegate.openGalleryForGif()
    }


    override fun doRevert(source: Uri?) {
        Glide.with(mContext).load(source).into(original)
//        Glide.with(mContext).load("").into(reversed)
        reversed.setImageBitmap(null)
        if (useNative) {
            withNativeRevert(source)
        } else if (type == 1) {
            withJavaRevert(source)
        } else {
            withFastRevert(source)
        }

        loading.visibility = View.VISIBLE
        result.text = "转换中 ......."
        timer.base = SystemClock.elapsedRealtime()
        timer.start()


    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun withFastRevert(source: Uri?) {
        GifRevertFactory.getReverseResFast(mContext, source)
                .subscribe({
                    loading.visibility = View.GONE

                    originalUrl = source
                    revertedlUrl = Uri.parse(it)
                    result.text = "图片保存在 :$it"
                    timer.stop()

                    Glide.with(mContext).load(it).into(reversed)
                }, {
                    Log.e("gif", it?.message ?: "")
                    Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                    loading.visibility = View.GONE
                    timer.stop()
                })
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun withJavaRevert(source: Uri?) {
        GifRevertFactory.getReverseRes(mContext, source)
                .subscribe({
                    loading.visibility = View.GONE

                    originalUrl = source
                    revertedlUrl = Uri.parse(it)
                    result.text = "图片保存在 :$it"
                    timer.stop()

                    Glide.with(mContext).load(it).into(reversed)
                }, {
                    Log.e("gif", it?.message ?: "")
                    Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                    loading.visibility = View.GONE
                    timer.stop()
                })
    }

    @SuppressLint("CheckResult")
    private fun withNativeRevert(source: Uri?) {
        FramesFactory.getReverseFrames(mContext, source)
                .map {
                    val lists = ArrayList<File>()
                    var wh: Pair<Int, Int> = Pair(0, 0)
                    var delay = 0
                    if (it.isNotEmpty()) {
                        for (frame in it) {
                            lists.add(File(frame.path))
                            if (frame.delay >= 0) {
                                delay = frame.delay
                            }
                        }
                        wh = BitmapTool.getBitmapWH(it[0].path)
                        Log.e(TAG, "wh==$wh")
                        Log.e(TAG, "delay==$delay")
                    }
                    for (list in lists) {
                        Log.e(TAG, "file==${list.absolutePath}")
                    }

                    val dest = Environment.getExternalStorageDirectory().toString() + File.separator + "test.gif"

                    val gifflen = Gifflen.Builder()
                            .delay(delay)
                            .width(wh.first)
                            .height(wh.second)
                            .listener(object : Gifflen.OnEncodeFinishListener {
                                override fun onEncodeFinish(path: String?) {
                                    Glide.with(mContext)
                                            .load(path)
                                            .into(reversed)
                                }
                            })
                            .build()
                    gifflen.encode(dest, lists)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.visibility = View.GONE
                    timer.stop()
                }) {
                    Log.e(TAG, it.message)
                    loading.visibility = View.GONE
                    timer.stop()
                }
    }
}

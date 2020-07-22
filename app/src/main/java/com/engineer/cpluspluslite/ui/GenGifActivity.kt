package com.engineer.cpluspluslite.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.engineer.cpluspluslite.R
import com.engineer.gif.GifGenFactory
import com.engineer.gif.revert.ResFrame
import com.engineer.gif.video.VideoToFrames
import com.list.rados.fast_list.FastListAdapter
import com.list.rados.fast_list.bind
import com.list.rados.fast_list.update
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_gen_gif.*
import kotlinx.android.synthetic.main.resources_results_layout.*
import kotlinx.android.synthetic.main.round_image_item.view.*

class GenGifActivity : BaseActivity() {

    private var datas = ArrayList<String>()
    private lateinit var adapter: FastListAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gen_gif)


        share.setOnClickListener {
            activityDelegate.share(originalUrl, revertedUrl)
        }
        file.setOnClickListener {
            activityDelegate.openFileSystem()
        }

        go.setOnClickListener {
            activityDelegate.openGalleryForImages()
        }

        by_video.setOnClickListener {
            activityDelegate.openGalleryForVideo()
        }

        adapter = lists.bind(datas, R.layout.round_image_item) { path: String,_ ->
            Glide.with(mContext).load(path).placeholder(R.drawable.haha).into(image)
        }.layoutManager(GridLayoutManager(mContext, 3))
    }


    @SuppressLint("CheckResult", "SetTextI18n")
    override fun genGifFromImages(uris: List<String>) {
        lists.visibility = View.VISIBLE
        video_container.visibility = View.GONE
        datas.clear()
        datas.addAll(uris)
        lists.update(datas)
        originalUrl = Uri.parse(uris[0])
        val frames = ArrayList<ResFrame>()
        for (uri in uris) {
            Log.e(TAG, ": $uri")
            val frame = ResFrame(1000, uri)
            frames.add(frame)
        }

        loading.visibility = View.VISIBLE
        result.text = "转换中 ......."
        timer.base = SystemClock.elapsedRealtime()
        timer.start()
        GifGenFactory.genGifFastModeFromFile(frames)
                .subscribe {
                    result.text = "图片保存在 :$it"
                    hideLoading()
                    Glide.with(mContext).load(it).into(reversed)
                }
    }

    override fun genGifFromVideo(uri: Uri) {
        super.genGifFromVideo(uri)
        lists.visibility = View.GONE
        video_container.visibility = View.VISIBLE

        video_container.setVideoURI(uri)
        loading.visibility = View.VISIBLE
        result.text = "转换中 ......."
        timer.base = SystemClock.elapsedRealtime()
        timer.start()

        processVideo(uri)
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun processVideo(uri: Uri) {
        val path = activityDelegate.providePath("fly")
        val videoTo = VideoToFrames(path)
        Observable.create<ArrayList<Bitmap>> {
            val bitmaps = videoTo.genFramesFromVideo(uri, 0, 5 * 1000, 1 * 1000)
            Log.e(TAG, "bitmaps : ${bitmaps.size}")
            it.onNext(bitmaps)
        }.subscribeOn(Schedulers.io()).subscribe { it ->
            GifGenFactory.genGifFastModeFromBitmaps(it)
                    .subscribe({
                        hideLoading()
                        result.text = "图片保存在 :$it"
                        Glide.with(mContext).load(it).into(reversed)
                    }, { error ->
                        hideLoading()
                        Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show()
                        error.printStackTrace()
                    })
        }
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
        timer.stop()
    }
}

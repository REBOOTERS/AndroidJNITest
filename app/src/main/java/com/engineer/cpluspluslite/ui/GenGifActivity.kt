package com.engineer.cpluspluslite.ui

import android.annotation.SuppressLint
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
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_gen_gif.*
import kotlinx.android.synthetic.main.activity_gen_gif.file
import kotlinx.android.synthetic.main.activity_gen_gif.go
import kotlinx.android.synthetic.main.activity_gen_gif.loading
import kotlinx.android.synthetic.main.activity_gen_gif.result
import kotlinx.android.synthetic.main.activity_gen_gif.share
import kotlinx.android.synthetic.main.activity_gen_gif.timer
import kotlinx.android.synthetic.main.activity_reverse_gif.*
import kotlinx.android.synthetic.main.resources_results_layout.*
import kotlinx.android.synthetic.main.round_image_item.view.*

class GenGifActivity : BaseActivity() {

    private var datas = ArrayList<String>()
    private lateinit var adapter: FastListAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gen_gif)


        share.setOnClickListener {
            activityDelegate.share(originalUrl, revertedlUrl)
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

        adapter = lists.bind(datas, R.layout.round_image_item) { path: String ->
            Glide.with(mContext).load(path).placeholder(R.drawable.haha).into(image)
        }.layoutManager(GridLayoutManager(mContext, 3))
    }


    @SuppressLint("CheckResult")
    override fun genGifFromImages(uris: List<String>) {
        lists.visibility = View.VISIBLE
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

                    loading.visibility = View.GONE
                    result.text = "图片保存在 :$it"
                    timer.stop()
                    Glide.with(mContext).load(it).into(reversed)
                }
    }

    override fun genGifFromVideo(uri: Uri) {
        super.genGifFromVideo(uri)
        val path = activityDelegate.providePath("fly")
        val videoTo = VideoToFrames(path)
        val bitmaps = videoTo.genFramesformFromVideo(uri, 0, 5 , 5 * 1000)
        loading.visibility = View.VISIBLE
        result.text = "转换中 ......."
        timer.base = SystemClock.elapsedRealtime()
        timer.start()
        bitmaps?.apply {
            GifGenFactory.genGifFastModeFromBitmaps(this)
                    .subscribe({
                        loading.visibility = View.GONE
                        result.text = "图片保存在 :$it"
                        timer.stop()
                        Glide.with(mContext).load(it).into(reversed)
                    }, {
                        Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                        it.printStackTrace()
                    })

        }

    }
}

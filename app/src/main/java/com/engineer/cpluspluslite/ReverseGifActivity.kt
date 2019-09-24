package com.engineer.cpluspluslite

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.bumptech.glide.Glide
import com.engineer.gif.revert.FramesFactory
import com.engineer.gif.revert.GifFactory
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import com.wang.avi.indicators.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_reverse_gif.*
import kotlinx.android.synthetic.main.activity_reverse_gif.go
import kotlinx.android.synthetic.main.image_container.*
import java.io.File

class ReverseGifActivity : AppCompatActivity() {

    // <editor-fold defaultstate="collapsed" desc="onCreate">

    private var mHasPermission: Boolean = false

    private lateinit var mContext: Context
    private var originalUrl: Uri? = null
    private var revertedlUrl: Uri? = null
    private var useNative = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_reverse_gif)
        start.setOnClickListener { selectGif(false) }
        go.setOnClickListener { selectGif(true) }

        share.setOnClickListener {
            if (originalUrl != null && revertedlUrl != null) {
                val shareIntent = ShareCompat.IntentBuilder.from(this)
                        .addStream(originalUrl)
                        .addStream(revertedlUrl)
                        .setText("反转 gif")
                        .setType("text/richtext")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                                or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                                or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(shareIntent)
            } else {
                Toast.makeText(this, "请选择图片先，😁", Toast.LENGTH_SHORT).show()
            }
        }

//        Glide.with(this).load(R.drawable.haha).into(original)
//        Glide.with(this).load(R.drawable.haha_revert).into(reversed)
        val random = (Math.random() * indicators.size).toInt()
        av.indicator = indicators[random]
    }
    // </editor-fold>

    @SuppressLint("CheckResult")
    private fun selectGif(useNative: Boolean) {
        if (!mHasPermission) {
            return
        }
        this.useNative = useNative
        Matisse.from(this)
                .choose(MimeType.of(MimeType.GIF))
                .showSingleMediaType(true)
                .countable(false)
                .capture(false)
                .captureStrategy(
                        CaptureStrategy(true, mContext.packageName + ".fileprovider")
                )
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(Glide4Engine())
                .forResult(GIF_REQUEST_CODE)
    }


    private fun doRevert(source: String?) {
        Glide.with(mContext).load(source).into(original)
        if (useNative) {
            withNativeRevert(source)
        } else {
            withJavaRevert(source)
        }

        loading.visibility = View.VISIBLE
        result.text = "转换中 ......."
        timer.base = SystemClock.elapsedRealtime()
        timer.start()


    }

    @SuppressLint("CheckResult")
    private fun withJavaRevert(source: String?) {
        GifFactory.getReverseRes(mContext, source)
                .subscribe({
                    loading.visibility = View.GONE

                    originalUrl = Uri.parse(source)
                    revertedlUrl = Uri.parse(it)
                    result.text = "图片保存在 :$it"
                    timer.stop()

                    Glide.with(mContext).load(it).into(reversed)
                    // 原图和反转图同时加载，看看效果
                }, {
                    it.printStackTrace()
                    loading.visibility = View.GONE
                    timer.stop()
                })
    }

    @SuppressLint("CheckResult")
    private fun withNativeRevert(source: String?) {
        FramesFactory.getReverseFrames(mContext, source)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val lists = ArrayList<File>()
                    if (it != null) {
                        for (frame in it) {
                            lists.add(File(frame.path))
                        }
                    }
                    val dest = Environment.getExternalStorageDirectory().toString() + File.separator + "test.gif"

                    val gifflen = Gifflen.Builder()
                            .listener(object : Gifflen.OnEncodeFinishListener {
                                override fun onEncodeFinish(path: String?) {
                                    Glide.with(mContext)
                                            .load(path)
                                            .into(reversed)
                                }
                            })
                            .build()
                    gifflen.encode(dest, lists)
                }, {
                    it.printStackTrace()
                    loading.visibility = View.GONE
                    timer.stop()
                })
    }


    // <editor-fold defaultstate="collapsed" desc="onActivityResult">
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GIF_REQUEST_CODE) {
            val result = Matisse.obtainPathResult(data)[0]
            if (result.endsWith(".gif")) {
                doRevert(result)
            } else {
                Toast.makeText(this, "not gif", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="revert drawable">
    @SuppressLint("CheckResult")
    private fun doRevert(source: Int?) {
        loading.visibility = View.VISIBLE
        GifFactory.getReverseRes(mContext, source)
                .subscribe({
                    loading.visibility = View.GONE
                    Glide.with(mContext).load(it).into(reversed)
                    // 原图和反转图同时加载，看看效果
                    Glide.with(mContext).load(source).into(original)
                }, {
                    it.printStackTrace()
                })
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="companion object">
    companion object {
        val GIF_REQUEST_CODE = 100
        val indicators = arrayListOf(
                BallClipRotateIndicator(), CubeTransitionIndicator(),
                SquareSpinIndicator(), LineScaleIndicator(),
                TriangleSkewSpinIndicator(), PacmanIndicator(),
                SemiCircleSpinIndicator()
        )
    }

    override fun onResume() {
        super.onResume()
        if (!mHasPermission) {
            requestPermission()
        }
    }


    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RealRxPermission.getInstance(mContext).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { permission -> mHasPermission = permission.state() == Permission.State.GRANTED }
    }
    // </editor-fold>
}

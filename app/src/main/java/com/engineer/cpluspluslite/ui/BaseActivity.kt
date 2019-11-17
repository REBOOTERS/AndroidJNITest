package com.engineer.cpluspluslite.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.engineer.cpluspluslite.delegate.ActivityDelegate
import com.engineer.cpluspluslite.delegate.ActivityDelegate.Companion.GIF_REQUEST_CODE
import com.engineer.cpluspluslite.delegate.ActivityDelegate.Companion.IMAGES_REQUEST_CODE
import com.engineer.cpluspluslite.delegate.ActivityDelegate.Companion.VIDEO_REQUEST_CODE
import com.engineer.gif.GifRevertFactory
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_reverse_gif.*
import kotlinx.android.synthetic.main.image_container.*

/**
 * @author rookie
 * @since 11-17-2019
 */

open class BaseActivity : AppCompatActivity() {

    protected var mHasPermission: Boolean = false
    protected lateinit var mContext: Context

    protected var revertedlUrl: Uri? = null
    protected var originalUrl: Uri? = null

    lateinit var activityDelegate: ActivityDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        activityDelegate = ActivityDelegate(this)
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


    // <editor-fold defaultstate="collapsed" desc="onActivityResult">
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                GIF_REQUEST_CODE -> {
                    val result = Matisse.obtainPathResult(data)[0]
                    val uri = Matisse.obtainResult(data)[0]
                    if (result.endsWith(".gif")) {
                        doRevert(uri)
                    } else {
                        Toast.makeText(this, "not gif", Toast.LENGTH_SHORT).show()
                    }
                }
                IMAGES_REQUEST_CODE -> {
                    val uris = Matisse.obtainPathResult(data)
                    genGifFromImages(uris)
                }
                VIDEO_REQUEST_CODE -> {
                    val uri = Matisse.obtainResult(data)[0]
                    genGifFromVideo(uri)
                }
            }
        }
    }


    // </editor-fold>

    protected open fun genGifFromImages(uris: List<String>) {}
    protected open fun genGifFromVideo(uri: Uri) {}
    protected open fun doRevert(uri: Uri?) {}

    // <editor-fold defaultstate="collapsed" desc="revert drawable">
    @SuppressLint("CheckResult")
    private fun doInternalRevert(source: Int?) {
        loading.visibility = View.VISIBLE
        GifRevertFactory.getReverseRes(mContext, source)
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

    companion object {
        val TAG = "gif-revert"
    }
}
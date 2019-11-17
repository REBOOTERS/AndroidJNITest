package com.engineer.cpluspluslite.delegate

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ShareCompat
import com.wang.avi.indicators.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine

/**
 * @author zhuyongging @ Zhihu Inc.
 * @since 11-17-2019
 */
class ActivityDelegate(private val context: Activity) {

    fun openGalleryForGif() {
        Matisse.from(context)
                .choose(MimeType.of(MimeType.GIF))
                .showSingleMediaType(true)
                .countable(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(GIF_REQUEST_CODE)
    }

    fun openGalleryForVideo() {
        Matisse.from(context)
                .choose(MimeType.ofVideo())
                .showSingleMediaType(true)
                .countable(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(VIDEO_REQUEST_CODE)
    }

    fun openGalleryForImages() {
        Matisse.from(context)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP))
                .autoHideToolbarOnSingleTap(true)
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(100)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(IMAGES_REQUEST_CODE)
    }

    fun openFileSystem() {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/gif"
//            intent.data = Uri.fromFile(path)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun share(originalUrl: Uri?, revertedUrl: Uri?) {
        if (revertedUrl != null) {
            val shareIntent = ShareCompat.IntentBuilder.from(context)
                    .addStream(originalUrl)
                    .addStream(revertedUrl)
                    .setText("反转 gif")
                    .setType("image/gif")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                            or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                            or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } else {
            Toast.makeText(context, "请选择图片先，😁", Toast.LENGTH_SHORT).show()
        }
    }

    // <editor-fold defaultstate="collapsed" desc="companion object">
    companion object {
        val GIF_REQUEST_CODE = 100
        val VIDEO_REQUEST_CODE = 101
        val IMAGES_REQUEST_CODE = 102
        val indicators = arrayListOf(
                BallClipRotateIndicator(), CubeTransitionIndicator(),
                SquareSpinIndicator(), LineScaleIndicator(),
                TriangleSkewSpinIndicator(), PacmanIndicator(),
                SemiCircleSpinIndicator()
        )
    }
    // </editor-fold>
}
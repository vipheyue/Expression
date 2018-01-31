package com.lightworld.expression

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lightworld.expression.wxapi.ShareUtils
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File


class PullToRefreshAdapter : BaseQuickAdapter<String, BaseViewHolder> {
    var mActivity: Activity
    constructor(mActivity: Activity, layoutResId: Int, data: List<String>?) : super(layoutResId, data) {
        this.mActivity = mActivity
    }

    override fun convert(helper: BaseViewHolder, itemUrl: String) {
        val view = helper.getView<ImageView>(R.id.imageView)

        val options = RequestOptions()
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(MyApplication.get())
                .load(itemUrl)
                .apply(options)
                .into(view)
        view.setOnClickListener {
            var intent = Intent(view.context, PreviewImgActivity::class.java)
            intent.putExtra("url", itemUrl)
            mActivity.startActivity(intent)
        }
        helper.getView<ImageView>(R.id.iv_wechat).setOnClickListener {
            val target = Glide.with(MyApplication.get())
                    .asBitmap()
                    .load(itemUrl)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val wxBitmap = resource.copy(resource.getConfig(), true)
                            ShareUtils.share2WX(mActivity, wxBitmap)
                        }
                    })

        }
        helper.getView<ImageView>(R.id.iv_qq).setOnClickListener {
            //TODO 写权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(view.context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dealQQShare(view, itemUrl)
                } else {
                    val requestArray: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(mActivity, requestArray, 0)
                }
            }

        }
    }

    private fun dealQQShare(view: ImageView, itemUrl: String) {
        val target = Glide.with(MyApplication.get())
                .asBitmap()
                .load(itemUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tempShare.png")
                        async(UI) {
                            val path: Deferred<String> = bg {
                                BitmapUtils.BitmapToFile(resource, file)
                            }
                            ShareUtils.share2QQ(mActivity, path.await())
                        }
                    }
                })
    }


}

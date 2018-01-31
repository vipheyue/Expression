package com.lightworld.expression

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
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


class PullToRefreshAdapter(layoutResId: Int, data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, itemUrl: String) {
        val view = helper.getView<ImageView>(R.id.imageView)

        val options = RequestOptions()
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(view.context)
                .load(itemUrl)
                .apply(options)
                .into(view)
        view.setOnClickListener {
            var intent = Intent(view.context, PreviewImgActivity::class.java)
            intent.putExtra("url", itemUrl)
            view.context.startActivity(intent)
        }
        helper.getView<ImageView>(R.id.iv_wechat).setOnClickListener {
            val target = Glide.with(view.context as Activity)
                    .asBitmap()
                    .load(itemUrl)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val wxBitmap = resource.copy(resource.getConfig(), true)
                            ShareUtils.share2WX(view.context as Activity, wxBitmap)
                        }
                    })

        }
        helper.getView<ImageView>(R.id.iv_qq).setOnClickListener {
            val target = Glide.with(view.context as Activity)
                    .asBitmap()
                    .load(itemUrl)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tempShare.png")
                            async(UI) {
                                val path: Deferred<String> = bg {
                                    BitmapUtils.BitmapToFile(resource, file)
                                }
                                ShareUtils.share2QQ(view.context as Activity, path.await())
                            }
                        }
                    })
        }
    }


}

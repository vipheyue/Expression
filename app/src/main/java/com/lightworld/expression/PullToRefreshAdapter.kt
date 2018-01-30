package com.lightworld.expression

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lightworld.expression.wxapi.Constants
import com.lightworld.expression.wxapi.Util
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File


class PullToRefreshAdapter(layoutResId: Int, data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        val view = helper.getView<ImageView>(R.id.imageView)

        val options = RequestOptions()
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(view.context)
                .load(item)
                .apply(options)
                .into(view)
        helper.getView<ImageView>(R.id.iv_wechat).setOnClickListener {
            val target = Glide.with(view.context as Activity)
                    .asBitmap()
                    .load(item)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            share2WX(view.context as Activity, resource)
                        }
                    })

        }
        helper.getView<ImageView>(R.id.iv_qq).setOnClickListener {
            val target = Glide.with(view.context as Activity)
                    .asBitmap()
                    .load(item)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tempShare.png")
                            async(UI) {
                                val path: Deferred<String> = bg {
                                    BitmapUtils.BitmapToFile(resource, file)
                                }
                                share2QQ(view.context as Activity, path.await())
                            }

                        }
                    })
        }
    }


    private fun share2WX(mActivity: Activity, resource: Bitmap) {
        var api: IWXAPI? = null
        api = WXAPIFactory.createWXAPI(mActivity, Constants.APP_ID)

//        val resource = BitmapFactory.decodeResource(mActivity.resources, R.mipmap.ic_launcher)
        val imgObj = WXImageObject(resource)

        val msg = WXMediaMessage()
        msg.mediaObject = imgObj

        val thumbBmp = Bitmap.createScaledBitmap(resource, 150, 150, true)
        resource.recycle()
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true)

        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg

        req.scene = SendMessageToWX.Req.WXSceneSession
        api.sendReq(req)
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

    private fun share2QQ(mActivity: Activity, path: String) {

        Toast.makeText(mActivity, "QQ", Toast.LENGTH_SHORT).show()

        val params = Bundle()
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, "/storage/emulated/0/tencent/MicroMsg/WeiXin/mmexport1513309517455.jpg")
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mActivity.resources.getString(R.string.app_name))
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
        var mTencent: Tencent? = null
        mTencent = Tencent.createInstance(Constants.QQ_ID, mActivity)
        mTencent.shareToQQ(mActivity, params, object : IUiListener {
            override fun onCancel() {
            }

            override fun onComplete(response: Any) {
            }

            override fun onError(e: UiError) {

            }
        })

    }

}

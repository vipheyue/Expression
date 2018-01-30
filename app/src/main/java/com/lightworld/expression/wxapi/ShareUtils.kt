package com.lightworld.expression.wxapi

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import com.lightworld.expression.R
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

/**
 * Created by heyue on 2018/1/30.
 */
object ShareUtils {
    fun share2WX(mActivity: Activity, resource: Bitmap) {
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

    fun share2QQ(mActivity: Activity, path: String) {
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
package com.lightworld.expression

import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder


class PullToRefreshAdapter(layoutResId: Int, data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        var view = helper.getView<ImageView>(R.id.imageView)
        Glide.with(view.context)
                .load(item)
                .into(view)
        helper.getView<ImageView>(R.id.iv_wechat).setOnClickListener {

            Toast.makeText(view.context,"WX",Toast.LENGTH_SHORT).show()
        }
        helper.getView<ImageView>(R.id.iv_qq).setOnClickListener {
            Toast.makeText(view.context,"QQ",Toast.LENGTH_SHORT).show()
        }
    }
}

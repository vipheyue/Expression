package com.lightworld.expression

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lightworld.expression.wxapi.ShareUtils
import kotlinx.android.synthetic.main.activity_look_img.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PreviewImgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look_img)
        initView()
    }

    private fun initView() {

        val mActionBar = supportActionBar
        mActionBar!!.setHomeButtonEnabled(true)
        mActionBar.setDisplayHomeAsUpEnabled(true)
        mActionBar.title = "返回"


        val url = intent.getStringExtra("url")
        Glide.with(MyApplication.get())
                .load(url)
                .into(imageView)
        imageView.setOnClickListener { finish() }
        fab_wx.setOnClickListener {
            val target = Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val wxBitmap = resource.copy(resource.getConfig(), true)
                            ShareUtils.share2WX(this@PreviewImgActivity, wxBitmap)
                        }
                    })
        }

        fab_qq.setOnClickListener {
            dealQQShare(url)
        }
        fab_share_expression.setOnClickListener {


            val target = Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tempShare.png")
                            async(UI) {
                                val path: Deferred<String> = bg {
                                    BitmapUtils.BitmapToFile(resource, file)
                                }
                                val imageUri = FileProvider.getUriForFile(this@PreviewImgActivity, "com.lightworld.expression.fileprovider", file)
                                var shareIntent: Intent = Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                shareIntent.setType("image/*");
                                startActivity(Intent.createChooser(shareIntent, "分享图片"))
                            }
                        }
                    })
        }
        fab_down.setOnClickListener {
            downPic(url)
        }
        fab_share_app.setOnClickListener {
            var textIntent = Intent(Intent.ACTION_SEND)
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content))
            startActivity(Intent.createChooser(textIntent, getString(R.string.app_name)))
        }

    }

    private fun dealQQShare(url: String?) {
        val target = Glide.with(this)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tempShare.png")
                        async(UI) {
                            val path: Deferred<String> = bg {
                                BitmapUtils.BitmapToFile(resource, file)
                            }
                            ShareUtils.share2QQ(this@PreviewImgActivity, path.await())
                        }

                    }
                })
    }

    private fun downPic(url: String?) {
        val target = Glide.with(this)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), timeStamp + ".png")
                        async(UI) {
                            val path: Deferred<String> = bg {
                                BitmapUtils.BitmapToFile(resource, file)
                            }
                            galleryAddPic(file.absolutePath)
                            Toast.makeText(this@PreviewImgActivity, "保存成功", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    }
                })
    }

    private fun galleryAddPic(path: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(path)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}

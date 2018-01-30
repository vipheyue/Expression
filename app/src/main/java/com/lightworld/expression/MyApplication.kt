package com.lightworld.expression

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport


/**
 * Created by heyue on 2017/12/19.
 */

class MyApplication : Application() {
    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, "5719048f8a", false);
    }
    companion object {
        private lateinit var INSTANCE: MyApplication

        fun get(): MyApplication = INSTANCE
    }
}
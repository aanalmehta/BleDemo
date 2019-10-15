package com.android.bledemo

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onTerminate() {
        super.onTerminate()
        if (instance != null) {
            instance = null
        }
    }

    companion object {
        var instance: App? = null
    }
}

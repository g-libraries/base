package com.core.base

import android.app.Application
import com.core.base.BuildConfig
import com.core.base.logging.ReleaseTree
import timber.log.Timber

open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        else
            Timber.plant(ReleaseTree())
    }
}
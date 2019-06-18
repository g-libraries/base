package com.eaterytemplate

import android.app.Application
import com.eaterytemplate.logging.ReleaseTree
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
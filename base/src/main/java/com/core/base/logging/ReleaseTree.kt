package com.core.base.logging

import timber.log.Timber
import java.util.logging.Level

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        //todo crashlytic logs
    }
}
package com.core.base.usecases

import okhttp3.Interceptor
import okhttp3.Response

class ErrorHandleInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (!response.isSuccessful) {
            RxBusGlobal.publish(EventServerError(response.code()))
        }

        return response
    }
}
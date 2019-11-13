package com.core.base.usecases

import android.os.Handler
import android.view.View


class OnOneClickListener(private val onOneClick: (View) -> Unit) : View.OnClickListener {
    companion object {
        const val NEXT_CLICK_DELAY = 2000L // ms
    }

    private var clickable = true

    override fun onClick(v: View) {
        if (clickable) {
            clickable = false
            onOneClick(v)
            Handler().postDelayed(
                { this.reset() },
                NEXT_CLICK_DELAY
            )
        }
    }

    /**
     * Make public and invoke to allow another clicks manually.
     */
    private fun reset() {
        clickable = true
    }
}


/**
 * Extension for Views to use OneClickListener easily
 */
fun View.setOnOneClickListener(onOneClick: (View) -> Unit) {
    val oneClickListener = OnOneClickListener {
        onOneClick(it)
    }
    setOnClickListener(oneClickListener)
}

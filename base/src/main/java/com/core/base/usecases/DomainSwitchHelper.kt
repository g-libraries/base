package com.core.base.usecases

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.view.View
import android.widget.Toast

class DomainSwitchHelper(
    val sharedPreferences: SharedPreferences,
    val context: Context,
    val build: Boolean
) {

    val MODE = "mode"

    fun initSwitch(view: View) {
        var isOk = true
        var counter = 0
        val handler = Handler()
        view.setOnClickListener {
            counter++

            handler.postDelayed({
                isOk = false
            }, 1000)

            if (!isOk)
                counter = 0

            handler.removeCallbacksAndMessages(null)

            if (counter > 10) {
                setMode(!getMode())

                Toast.makeText(
                    context,
                    if (getMode()) "Production. Reload the app" else "Development. Reload the app",
                    Toast.LENGTH_SHORT
                ).show()

                counter = 0
            }
        }
    }


    fun setMode(isProd: Boolean) {
        sharedPreferences.edit().putBoolean(MODE, isProd).apply()
    }

    fun getMode(): Boolean {
        return sharedPreferences.getBoolean(MODE, !build)
    }

}
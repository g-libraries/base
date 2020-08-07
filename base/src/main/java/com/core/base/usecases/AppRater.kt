package com.core.base.usecases

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.annotation.StyleRes
import javax.inject.Inject

class AppRater(
    val appContext: Context,
    val playMarketId: String,
    @StyleRes
    val themeId: Int,
    val title: String,
    val message: String,
    val positiveText: String,
    val negativeText: String,
    val neutralText: String
) {
    private val DAYS_UNTIL_PROMPT = 3

    val dontShowAgainKey = "dontShowAgain"

    val prefs = appContext.getSharedPreferences("apprater", Context.MODE_PRIVATE)
    val editor = prefs.edit()

    fun checkForRate(
        predicate: (Long) -> Boolean = { firstLaunchDate ->
            System.currentTimeMillis() >=
                    firstLaunchDate + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)
        }
    ): Boolean {
        if (prefs.getBoolean(dontShowAgainKey, false)) {
            return false
        }

        // Increment launch counter
        val launchCount = prefs.getInt("launchCount", 0) + 1
        editor.putInt("launchCount", launchCount)

        // Get date of first launch
        var dateFirstLaunch: Long = 0

        if (launchCount == 1) {
            dateFirstLaunch = System.currentTimeMillis()
            editor.putLong("dateFirstLaunch", dateFirstLaunch)
        }

        editor.apply()

        // Wait at least n days before opening
        return predicate.invoke(dateFirstLaunch)
    }

    fun showRateDialog(activityContext: Context) {
        AlertDialog.Builder(activityContext, themeId)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { dialogInterface: DialogInterface, i: Int ->
                activityContext.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$playMarketId")
                    )
                )

                dontShowAgain()
            }.setNegativeButton(negativeText) { _: DialogInterface, i: Int ->
                dontShowAgain()
            }.setNeutralButton(neutralText) { _: DialogInterface, i: Int ->
                dontShowAgain()
            }.show()
    }

    fun dontShowAgain() {
        if (editor != null) {
            editor.putBoolean(dontShowAgainKey, true)
            editor.commit()
        }
    }
}
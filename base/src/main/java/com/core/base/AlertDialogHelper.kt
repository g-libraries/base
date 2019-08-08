package com.core.base

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.DialogTitle

object AlertDialogHelper {

    fun createAlert(
        context: Context,
        title: String,
        yesText: String,
        noText: String,
        yesExpr: () -> Unit,
        noExpr: () -> Unit
    ) =
        AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
            .setTitle(title)
            .setPositiveButton(
                yesText
            ) { _, _ -> yesExpr.invoke() }.setNegativeButton(
                noText
            ) { _, _ -> noExpr.invoke() }.create()
}
package com.core.base.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.core.util.PatternsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController

fun EditText.validatePhoneNumber(): Boolean {
    val phoneNumber = this.text.toString().replaceSpaces()

    return if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.startsWith("+380") || phoneNumber.length < 13) {
        false
    } else {
        android.util.Patterns.PHONE.matcher(phoneNumber).matches()
    }
}

fun String.tokenPattern() = "Bearer $this"

fun View.OnKeyListener.validateCodeInput(keyInput: Int): Boolean {

    val validInput = arrayOf(
        KeyEvent.KEYCODE_0,
        KeyEvent.KEYCODE_1,
        KeyEvent.KEYCODE_2,
        KeyEvent.KEYCODE_3,
        KeyEvent.KEYCODE_4,
        KeyEvent.KEYCODE_6,
        KeyEvent.KEYCODE_7,
        KeyEvent.KEYCODE_8,
        KeyEvent.KEYCODE_9
    )
    return validInput.contains(keyInput)
}

fun Float.dpToPx(displatMetrics: DisplayMetrics): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displatMetrics)

fun Char.isSpace(): Boolean = this.toString() == " "

fun String.replaceSpaces() = this.replace(" ", "")

fun String.addPlus() = if (this.startsWith("+")) this else "+$this"

fun String.validateName(): Boolean = this.length > 2

fun String.validateSurName(): Boolean = this.length > 2

fun String.validateEmail(): Boolean =
    this.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()

fun String.convertToPhoneNumberWithReplace(mask: String = "+XXX XX XXX XX XX"): String {
    val text = this.replaceSpaces().replace("+", "")

    val resultText = StringBuilder()
    resultText.append(mask)
    var xIndex: Int
    for (digit in text.toCharArray()) {
        xIndex = resultText.indexOf('X')
        resultText.setCharAt(xIndex, digit)
    }

    var end = resultText.indexOf('X')

    if (end == -1) {
        end = mask.length
    }

    return resultText.toString().substring(0, end)
}

fun FragmentManager.getVisibleFragment(): Fragment? {
    val fragments = this.fragments
    for (fragment in fragments) {
        if (fragment != null && fragment.isVisible)
            return fragment
    }
    return null
}

val Int.savingsProgress: Int
    get() = (this * 2000)

fun NavController.navigateClearStack(rootFragmentId: Int, destinationId: Int) {
    this.popBackStack(rootFragmentId, false)
    this.navigate(destinationId)
}

fun NavController.navigateClearLastInStack(destinationId: Int, bundle: Bundle? = null) {
    this.popBackStack()
    this.navigate(destinationId, bundle)
}

fun Boolean.toInt() = if (this) 1 else 0

val Int.toPx: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.toPx: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Float.toDp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Long.secToMs: Long
    get() = (this * 1000)

val Float.secToMs: Long
    get() = (this * 1000).toLong()

val String.packageName: String
    get() = (this.substring(0, this.lastIndexOf(".")))

fun Drawable.maskColor(color: Int) {
    this.mutate()
    this.clearColorFilter()
    this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun String.formatAsUrl() =
    if (!this.startsWith("http://") && !this.startsWith("https://")) "https://$this" else this

fun String.formatAsWorkingTime(work24h: String): CharSequence? {
    return if (this.count { it == '0' } >= 8) work24h else this
}

fun Float.formatWithoutZero(): String {
    return if (this.toInt().toFloat() == this) this.toInt().toString() else String.format(
        "%.2f",
        this
    )
}

fun Double.formatWithoutZero(): String {
    return if (this.toInt().toDouble() == this) this.toInt().toString() else String.format(
        "%.2f",
        this
    )
}

fun View?.applyGlobalLayoutListener(attachedExpr: (it: View?) -> Unit) {
    this?.viewTreeObserver?.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            this@applyGlobalLayoutListener.viewTreeObserver.removeOnGlobalLayoutListener(this)
            attachedExpr(this@applyGlobalLayoutListener)
        }
    }) ?: attachedExpr(null)
}

fun String.decodeUserGender(): Int {
    return when (this) {
        "male" -> 1
        "female" -> 2
        "unknown" -> 0
        else -> 0
    }
}

fun Int.encodeUserGender(): String {
    return when (this) {
        1 -> "male"
        2 -> "female"
        0 -> "unknown"
        else -> "unknown"
    }
}

@SuppressLint("MissingPermission")
fun Fragment.vibratePhone(duration: Long) {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(duration)
    }
}

fun EditText.setWhiteSpaceFilter() = run {
    this.filters = arrayOf(object : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            var keepOriginal = true
            val sb = StringBuilder(end - start)
            for (i in start until end) {
                val c = source[i]
                if (isCharAllowed(c))
                    sb.append(c) else keepOriginal = false
            }
            return if (keepOriginal) null else {
                if (source is Spanned) {
                    val sp = SpannableString(sb)
                    TextUtils.copySpansFrom(source, start, sb.length, null, sp, 0)
                    sp
                } else {
                    sb
                }
            }
        }

        private fun isCharAllowed(c: Char): Boolean {
            return Character.isLetterOrDigit(c)
        }
    })
}
package ru.zuma.unicornclub

import android.app.Activity
import android.support.v4.app.Fragment
import android.widget.Toast
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun launchPrintThrowable(runnable: suspend CoroutineScope.() -> Unit): Job {
    return launch {
        try {
            runnable()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

private val symbols: DecimalFormatSymbols by lazy {
    val dfs = DecimalFormatSymbols()
    dfs.groupingSeparator = ' '
    dfs
}
private val numberFormat = DecimalFormat("###,###", symbols)

fun viewFormat(num: Number?): String? = if (num != null) numberFormat.format(num) else null

fun Fragment.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(activity, text, duration).show()
}

fun Fragment.toastUI(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(activity, text, duration).show()
    }
}

fun Activity.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Activity.toastUI(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, text, duration).show()
    }
}

fun Activity.runOnUiThread(context: Activity? = this, action: () -> Unit) {
    context?.runOnUiThread(action)
}

fun Fragment.runOnUiThread(context: Activity? = activity, action: () -> Unit) {
    context?.runOnUiThread(action)
}
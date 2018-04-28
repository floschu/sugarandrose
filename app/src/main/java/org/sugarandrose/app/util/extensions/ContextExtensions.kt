/* Copyright 2017 Tailored Media GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package org.sugarandrose.app.util.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.squareup.picasso.Picasso
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers
import org.sugarandrose.app.R
import java.io.IOException
import java.util.*
import android.support.v4.content.ContextCompat.startActivity


fun Context.getCurrentLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    this.resources.configuration.locales.get(0)
} else {
    @Suppress("DEPRECATION")
    this.resources.configuration.locale
}

inline fun <reified T> Context.castWithUnwrap(): T? {
    if (this is T) return this

    var context = this
    while (context is ContextWrapper) {
        context = context.baseContext
        if (context is T) {
            return context
        }
    }
    return null
}

fun Context.rxPicasso(url: String?): Single<Bitmap> = Single.create { emitter: SingleEmitter<Bitmap> ->
    try {
        val bitmap = Picasso.with(this).load(url).get()
        emitter.onSuccess(bitmap)
    } catch (e: IOException) {
        emitter.onError(e)
    }
}.subscribeOn(Schedulers.io())

fun Context.openNotificationSettings() {
    val intent = Intent().apply {
        action = "android.settings.APP_NOTIFICATION_SETTINGS"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra("android.provider.extra.APP_PACKAGE", packageName)
        } else {
            putExtra("app_package", packageName)
            putExtra("app_uid", applicationInfo.uid)
        }
    }
    startActivity(intent)
}

fun Context.shareApp() {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app, "https://play.google.com/store/apps/details?id=org.sugarandrose.app"))
        type = "text/plain"
    }
    startActivity(sendIntent)
}

fun Context.areYouSureDialog(callback: () -> Unit) = AlertDialog.Builder(this, R.style.DialogTheme).apply {
    setTitle(getString(R.string.dialog_are_you_sure_title))
    setMessage(getString(R.string.dialog_are_you_sure_text))
    setPositiveButton(android.R.string.yes, { _, _ -> callback.invoke() })
    setNegativeButton(android.R.string.no, null)
    setCancelable(true)
    show()
}

val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

fun Context.hideKeyboard(inputView: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager.isActive) inputMethodManager.hideSoftInputFromWindow(inputView.windowToken, 0)
}

fun Context.showKeyboard(inputView: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(inputView, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.getColorHex(@ColorRes colorInt: Int): String = String.format("#%06X", (0xFFFFFF and ContextCompat.getColor(this, colorInt)))
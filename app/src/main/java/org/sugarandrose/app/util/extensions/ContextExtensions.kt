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

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.io.FileNotFoundException
import java.util.*
import android.content.Intent
import org.sugarandrose.app.R


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

fun Context.loadWithPicasso(url: String?): Single<Bitmap> = Single.create { emitter: SingleEmitter<Bitmap> ->
    Picasso.with(this).load(url).into(object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        override fun onBitmapFailed(errorDrawable: Drawable?) = emitter.onError(FileNotFoundException())
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) = emitter.onSuccess(bitmap)
    })
}

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

fun Context.areYouSureDialog(callback: () -> Unit) = AlertDialog.Builder(this).apply {
    setTitle(getString(R.string.dialog_are_you_sure))
    setPositiveButton(android.R.string.yes, { _, _ -> callback.invoke() })
    setNegativeButton(android.R.string.no, null)
    setCancelable(true)
    show()
}

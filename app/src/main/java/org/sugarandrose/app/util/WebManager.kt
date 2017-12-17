package org.sugarandrose.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerActivity
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class WebManager @Inject
constructor(@ActivityContext private val context: Context) {
    init {
        CustomTabsClient.connectAndInitialize(context, "com.android.chrome")
    }

    fun open(url: String) {
        if (url.isEmpty() || !(url.startsWith("http://") || url.startsWith("https://"))) return
        try {
            CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setShowTitle(true)
                    .addDefaultShareMenuItem()
                    .setCloseButtonIcon(getBitmapFromVectorDrawable(R.drawable.ic_arrow_back_white))
                    .build()
                    .launchUrl(context, Uri.parse(url))
        } catch (throwable: Throwable) {
            Timber.w(throwable)
            context.startActivity(Utils.web(url))
        }
    }

    private fun getBitmapFromVectorDrawable(@DrawableRes res: Int): Bitmap {
        val drawable = context.getDrawable(res)

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}

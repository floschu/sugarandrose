package org.sugarandrose.app.util.manager

import android.content.Context
import android.content.Intent
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
import org.sugarandrose.app.util.Utils
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

    fun open(uri: Uri) = open(uri.toString())

    fun open(url: String) {
        if (url.isEmpty() || !(url.startsWith("http://") || url.startsWith("https://"))) return
        try {
            CustomTabsIntent.Builder().apply {
                setToolbarColor(ContextCompat.getColor(context, R.color.colorAccent))
                setShowTitle(true)
                addDefaultShareMenuItem()
                setCloseButtonIcon(getBitmapFromVectorDrawable(R.drawable.ic_arrow_back))
                setStartAnimations(context, R.anim.fade_in, R.anim.fade_in)
                setExitAnimations(context, R.anim.fade_in, R.anim.fade_in)
            }.build().apply {
                intent.`package` = "com.android.chrome"
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }.launchUrl(context, Uri.parse(url))
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

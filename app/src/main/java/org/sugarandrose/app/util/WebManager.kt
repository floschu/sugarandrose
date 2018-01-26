package org.sugarandrose.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class WebManager @Inject
constructor(@ActivityContext private val context: Context, private val navigator: Navigator) {
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

    fun openFacebook(name: String, id: String) {
        val facebookUrl = "https://www.facebook.com/$name"
        val uri = try {
            val applicationInfo = context.applicationContext.packageManager.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) Uri.parse("fb://page/$id")
            else Uri.parse(facebookUrl)
        } catch (e: Exception) {
            Uri.parse(facebookUrl)
        }
        navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun openInstagram(name: String) {
        val uri = Uri.parse("http://instagram.com/_u/$name")
        try {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri).apply { `package` = "com.instagram.android" })
        } catch (e: Exception) {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun openPinterest(name: String) {
        try {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("pinterest://www.pinterest.com/$name")))
        } catch (e: Exception) {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/$name")))
        }
    }

    fun openTwitter(name: String, id: String) {
        val uri = try {
            context.applicationContext.packageManager.getPackageInfo("com.twitter.android", 0)
            Uri.parse("twitter://user?user_id=$id")
        } catch (e: Exception) {
            Uri.parse("https://twitter.com/$name")
        }
        navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

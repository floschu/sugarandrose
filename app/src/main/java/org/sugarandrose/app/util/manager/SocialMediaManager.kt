package org.sugarandrose.app.util.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class SocialMediaManager @Inject
constructor(@ActivityContext private val context: Context,
            private val navigator: Navigator,
            private val webManager: WebManager
) {

    fun openFacebook(name: String, id: String) {
        val facebookUrl = "https://www.facebook.com/$name"
        try {
            val applicationInfo = context.applicationContext.packageManager.getApplicationInfo("com.facebook.katana", 0)
            val uri = if (applicationInfo.enabled) Uri.parse("fb://page/$id")
            else Uri.parse(facebookUrl)
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            webManager.open(facebookUrl)
        }
    }

    fun openInstagram(name: String) {
        val uri = Uri.parse("http://instagram.com/_u/$name")
        try {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri).apply { `package` = "com.instagram.android" })
        } catch (e: Exception) {
            webManager.open(uri)
        }
    }

    fun openPinterest(name: String) {
        try {
            navigator.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("pinterest://www.pinterest.com/$name")))
        } catch (e: Exception) {
            webManager.open("https://www.pinterest.com/$name")
        }
    }

    fun openTwitter(name: String, id: String) {
        try {
            context.applicationContext.packageManager.getPackageInfo("com.twitter.android", 0)
            navigator.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=$id")))
        } catch (e: Exception) {
            webManager.open("https://twitter.com/$name")
        }
    }
}
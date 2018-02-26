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

package org.sugarandrose.app.util.bindingadapters

import android.app.Activity
import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.threeten.bp.format.DateTimeFormatter
import android.webkit.WebView
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.ui.photo.PhotoDetailActivity
import org.sugarandrose.app.util.extensions.fromRealmString


@BindingMethods(BindingMethod(type = SwipeRefreshLayout::class, attribute = "onRefresh", method = "setOnRefreshListener"))
object BindingAdapters {

    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("android:onClick")
    @JvmStatic
    fun setOnClickListener(v: View, runnable: Runnable) {
        v.setOnClickListener { runnable.run() }
    }

    @BindingAdapter("date")
    @JvmStatic
    fun setDateText(v: TextView, date: String) {
        v.text = date.fromRealmString().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageSrc(view: ImageView, @DrawableRes src: Int) {
        view.setImageResource(src)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageWithPicassoString(view: ImageView, path: String?) {
        if (!path.isNullOrEmpty()) {
            Picasso.with(view.context.applicationContext).load(path).fit().centerCrop().into(view)
        } else view.setImageDrawable(null)
    }

    @BindingAdapter("loadUrl")
    @JvmStatic
    fun loadUrl(view: WebView, url: String) {
        view.loadUrl(url)
    }

    @BindingAdapter("loadWebContent", "css")
    @JvmStatic
    fun loadWebContent(view: WebView, content: String, css: String? = null) {
        var html = "<html>"
        if (css != null) html += "<head>$css</head>"
        html += "$content</html>"
        view.loadData(html, "text/html; charset=UTF-8", null)
    }

    @BindingAdapter("setColorScheme")
    @JvmStatic
    fun setColorScheme(view: SwipeRefreshLayout, enable: Boolean) {
        if (enable) view.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccentRipple, R.color.colorAccent)
    }

    @BindingAdapter("htmlText")
    @JvmStatic
    fun setHtmlText(view: TextView, text: String) {
        @Suppress("DEPRECATION")
        view.text = Html.fromHtml(text)
    }

    @BindingAdapter("logoClick")
    @JvmStatic
    fun setLogoClick(view: View, enable: Boolean) {
        view.setOnClickListener { SugarAndRoseApp.appComponent.webManager().open(BuildConfig.WEB_PAGE) }
    }

    @BindingAdapter("openPhotoDetail")
    @JvmStatic
    fun setOpenPhotoDetail(view: ImageView, path: String?) {
        if (path == null) return
        view.setOnClickListener {
            val intent = Intent(view.context, PhotoDetailActivity::class.java)
            intent.putExtra(PhotoDetailActivity.EXTRA_IMG_URL_AND_TRANSITION_NAME, path)
            if (view.context is Activity) {
                ActivityCompat.startActivity(view.context, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        view.context as Activity,
                        view,
                        path
                ).toBundle())
            } else view.context.startActivity(intent)
        }
    }
}
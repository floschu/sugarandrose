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

import android.databinding.BindingAdapter
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.support.v4.widget.SwipeRefreshLayout
import android.text.Html
import android.view.View
import android.webkit.WebChromeClient
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import android.webkit.WebView
import android.widget.CalendarView
import org.threeten.bp.LocalDate


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

    @BindingAdapter("fromHtml")
    @JvmStatic
    fun setHtmlText(v: TextView, html: String) {
        v.text = Html.fromHtml(html)
    }

    @BindingAdapter("date")
    @JvmStatic
    fun setDateText(v: TextView, date: ZonedDateTime) {
        v.text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    @BindingAdapter("android:src", "onImageLoaded", requireAll = false)
    @JvmStatic
    fun setImageWithPicasso(view: ImageView, path: String?, onImageLoadedRunnable: Runnable?) {
        if (path != null && path.isNotEmpty())
            Picasso.with(view.context.applicationContext).load(path).into(view, object : Callback {
                override fun onError() {
                    onImageLoadedRunnable?.run()
                }

                override fun onSuccess() {
                    onImageLoadedRunnable?.run()
                }
            })
        else onImageLoadedRunnable?.run()
    }

    @BindingAdapter("loadUrl")
    @JvmStatic
    fun loadUrl(view: WebView, url: String) {
        view.loadUrl(url)
    }

    @BindingAdapter("onDateSelect")
    @JvmStatic
    fun onDateSelect(view: CalendarView, callback: (LocalDate) -> Unit) {
        view.setOnDateChangeListener { _, year, month, dayOfMonth -> callback.invoke(LocalDate.of(year, month + 1, dayOfMonth)) }
    }
}
package org.sugarandrose.app.data.model.local

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.sugarandrose.app.R

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
enum class TextSearchFilter(@StringRes val titleResId: Int, @StringRes val hintResId: Int,@DrawableRes val drawableResId: Int) {
    POSTS(R.string.search_posts,R.string.search_posts_hint, R.drawable.ic_format_align_left),
    MEDIA(R.string.search_media, R.string.search_media_hint,R.drawable.ic_image),
    ROSES(R.string.search_roses,R.string.search_roses_hint, R.drawable.ic_rose)
}
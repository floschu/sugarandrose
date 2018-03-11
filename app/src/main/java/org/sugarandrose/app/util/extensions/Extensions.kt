package org.sugarandrose.app.util.extensions

import android.app.Activity
import android.content.res.Resources
import android.support.annotation.ColorRes
import android.view.View
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

fun runOnMainThread(block: () -> Unit): Disposable = Completable.fromAction(block).subscribeOn(AndroidSchedulers.mainThread()).subscribe()

fun Resources.getColorHex(@ColorRes colorInt: Int): String = String.format("#%06X", (0xFFFFFF and getColor(colorInt)))



fun Activity.showSystemUi() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}

fun Activity.hideStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_FULLSCREEN
}


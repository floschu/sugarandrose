package org.sugarandrose.app.util.extensions

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

//ViewModel

fun <V : MvvmView> MvvmViewModel<V>.attachViewOrThrowRuntimeException(view: MvvmView, savedInstanceState: Bundle?) {
    try {
        @Suppress("UNCHECKED_CAST")
        this.attachView(view as V, savedInstanceState)
    } catch (e: ClassCastException) {
        if (this !is NoOpViewModel<*>) {
            throw RuntimeException(javaClass.simpleName + " must implement MvvmView subclass as declared in " + this.javaClass.simpleName)
        }
    }
}

// Activity

fun Activity.showSystemUi() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}

fun Activity.hideStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_FULLSCREEN
}

//Bundle

fun <T : Parcelable> Bundle.getParcelable(key: String, defaultObject: T): T = if (containsKey(key)) {
    getParcelable(key)!!
} else {
    defaultObject
}
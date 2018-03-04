package org.sugarandrose.app.util.extensions

import android.view.View
import android.view.animation.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */


fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

fun View.slideOut(millis: Long): Completable {
    this.clearAnimation()
    val fadeOut = TranslateAnimation(Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 3f)
    fadeOut.interpolator = AccelerateInterpolator()
    fadeOut.duration = millis
    this.startAnimation(fadeOut)
    return Single.timer(millis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .toCompletable()
            .doOnComplete {
                this@slideOut.clearAnimation()
                this@slideOut.visibility = View.INVISIBLE
            }
            .doOnError(Timber::e)
}

fun View.slideIn(millis: Long): Completable {
    this.clearAnimation()
    val fadeIn =  TranslateAnimation(Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f, Animation.RELATIVE_TO_SELF, 3f, Animation.RELATIVE_TO_SELF, 0f)
    fadeIn.interpolator = DecelerateInterpolator()
    fadeIn.duration = millis
    this.startAnimation(fadeIn)
    return Single.timer(millis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .toCompletable()
            .doOnComplete {
                this@slideIn.clearAnimation()
                this@slideIn.visibility = View.VISIBLE
            }
            .doOnError(Timber::e)
}

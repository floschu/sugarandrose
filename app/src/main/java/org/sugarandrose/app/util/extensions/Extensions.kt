package org.sugarandrose.app.util.extensions

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

fun runOnMainThread(block: () -> Unit): Disposable = Completable.fromAction(block).subscribeOn(AndroidSchedulers.mainThread()).subscribe()

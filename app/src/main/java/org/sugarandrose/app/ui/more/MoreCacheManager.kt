package org.sugarandrose.app.ui.more

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.sugarandrose.app.data.model.LocalMorePage
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.injection.scopes.PerApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerApplication
class MoreCacheManager @Inject
constructor(private val api: SugarAndRoseApi) {
    val MORE_PAGES = listOf(
            api.getMore(1635), //rezeptindex
            api.getMore(2887),  //rosenglossar
            api.getMore(10030),  //rosenindex
            api.getMore(129) //diy
    )

    private var data = emptyList<LocalMorePage>()
        set(value) {
            field = value
            dataSubject.onNext(value)
        }

    var reloading = false
    val dataSubject: BehaviorSubject<List<LocalMorePage>> = BehaviorSubject.createDefault(data)

    fun reloadData(onError: (Throwable) -> Unit): Disposable =
            if (data.isEmpty()) Single.concat(MORE_PAGES)
                    .map(::LocalMorePage)
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { reloading = true }
                    .doOnEvent { _, _ -> reloading = false }
                    .subscribe({ data = it }, onError::invoke)
            else Completable.fromAction { dataSubject.onNext(data) }.subscribe()
}
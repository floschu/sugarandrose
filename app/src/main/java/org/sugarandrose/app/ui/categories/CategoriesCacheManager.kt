package org.sugarandrose.app.ui.categories

import com.google.gson.annotations.Until
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.injection.scopes.PerApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */


@PerApplication
class CategoriesCacheManager @Inject
constructor(private val api: SugarAndRoseApi) {

    private var data = emptyList<LocalCategory>()
        set(value) {
            field = value
            dataSubject.onNext(value)
        }

    val dataSubject: BehaviorSubject<List<LocalCategory>> = BehaviorSubject.createDefault(data)

    fun reloadData(onError: (Throwable) -> Unit): Disposable =
            if (data.isEmpty()) api.getCategories().observeOn(AndroidSchedulers.mainThread())
                    .map(this::mapParents)
                    .subscribe({ data = it }, onError::invoke)
            else Completable.fromAction { dataSubject.onNext(data) }.subscribe()

    private fun mapParents(cats: List<Category>): List<LocalCategory> =
            cats.filter { it.parent == 0 }
                    .map { LocalCategory(it, emptyList()) }
                    .sortedBy { it.name }
                    .also { it.forEach { mapChildren(it, cats) } }

    private fun mapChildren(parent: LocalCategory, cats: List<Category>) {
        parent.children = cats
                .filter { it.parent == parent.id }
                .map {
                    LocalCategory(it, emptyList())
                            .apply { mapChildren(this, cats) }
                }
                .sortedBy { it.name }
    }
}
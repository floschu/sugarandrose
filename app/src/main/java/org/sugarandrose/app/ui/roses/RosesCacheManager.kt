package org.sugarandrose.app.ui.roses

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.jsoup.Jsoup
import org.sugarandrose.app.data.model.local.LocalDisplayHeader
import org.sugarandrose.app.data.model.local.LocalDisplayItem
import org.sugarandrose.app.data.model.local.LocalRose
import org.sugarandrose.app.data.model.remote.Roses
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.injection.scopes.PerApplication
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerApplication
class RosesCacheManager @Inject
constructor(private val api: SugarAndRoseApi) {
    private var data = emptyList<LocalDisplayItem>()
        set(value) {
            field = value
            dataSubject.onNext(value)
        }

    var reloading = false
    val dataSubject: BehaviorSubject<List<LocalDisplayItem>> = BehaviorSubject.createDefault(data)

    fun checkReloadData(): Disposable = if (data.isEmpty()) api.getRoses()
            .map(this::mapToLocalRoses)
            .map(this::addHeaders)
            .doOnSubscribe { reloading = true }
            .doOnEvent { _, _ -> reloading = false }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data = it }, Timber::e)
    else Completable.fromAction { dataSubject.onNext(data) }.subscribe()

    private fun mapToLocalRoses(roses: Roses): List<LocalRose> = Jsoup
            .parse(roses.content.rendered)
            .getElementsByTag("img")
            .map {
                val id = it.attr("data-attachment-id").toLong()
                val image = it.attr("src")
                val name = it.attr("data-image-title")
                LocalRose(id, image, name)
            }

    private fun addHeaders(roses: List<LocalRose>): List<LocalDisplayItem> {
        val result = ArrayList<LocalDisplayItem>()
        roses.groupBy { it.category }.toSortedMap().forEach {
            result.add(LocalDisplayHeader(it.key))
            result.addAll(it.value)
        }
        return result
    }
}
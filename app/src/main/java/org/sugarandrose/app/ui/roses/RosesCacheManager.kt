package org.sugarandrose.app.ui.roses

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import org.jsoup.Jsoup
import org.sugarandrose.app.data.model.LocalDisplayHeader
import org.sugarandrose.app.data.model.LocalDisplayItem
import org.sugarandrose.app.data.model.LocalRose
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

    fun checkReloadData(disposable: CompositeDisposable) {
        if (data.isEmpty()) api.getRoses()
                .map(this::mapToLocalRoses)
                .map(this::addHeaders)
                .doOnSubscribe { reloading = true }
                .doOnEvent { _, _ -> reloading = false }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data = it }, Timber::e)
                .addTo(disposable)
    }

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
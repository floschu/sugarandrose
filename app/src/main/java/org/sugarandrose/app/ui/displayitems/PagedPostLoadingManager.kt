package org.sugarandrose.app.ui.displayitems

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.model.remote.Post
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_DEFAULT
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.data.remote.parseMaxPages
import org.sugarandrose.app.injection.scopes.PerFragment
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class PagedPostLoadingManager {
    private val api = SugarAndRoseApp.appComponent.sugarAndRoseApi()

    private var maximumNumberOfPostPages = TOTAL_PAGES_DEFAULT
    private var currentPostsPage = 1

    fun resetPages() {
        currentPostsPage = 1
    }

    fun loadPostsPage() = loadPage(api.getPostsPage(currentPostsPage))
    fun loadQueryPage(query: String) = loadPage(api.getPostsForQuery(query, currentPostsPage))
    fun loadCategoryPage(category: LocalCategory) = loadPage(api.getPostsForCategory(category.id))

    private fun loadPage(loadingSingle: Single<Result<List<Post>>>): Single<List<LocalPost>> =
            Single.just(currentPostsPage > maximumNumberOfPostPages)
                    .flatMap {
                        if (it) Single.just(emptyList())
                        else loadingSingle
                                .doOnSubscribe { currentPostsPage++ }
                                .doOnSuccess { maximumNumberOfPostPages = parseMaxPages(it) }
                                .map { it.response()?.body() }
                    }
                    .flattenAsFlowable { it }
                    .flatMapSingle { post ->
                        if (post.featured_media != 0L) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                        else Single.just(LocalPost(post))
                    }
                    .toList()
                    .map { it.sortedByDescending { it.date } }
}
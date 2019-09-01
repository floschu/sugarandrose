package org.sugarandrose.app.ui.displayitems

import io.reactivex.Single
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.model.remote.Post
import retrofit2.adapter.rxjava2.Result

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class PagedPostLoadingManager {
    private val api = SugarAndRoseApp.appComponent.sugarAndRoseApi()

    private var page = 1
    private val perPage = 14
    private var maximumNumberOfPostPages = 1

    fun resetPages() {
        page = 1
        maximumNumberOfPostPages = perPage
    }

    fun loadPostsPage() = loadPage(api.getPosts(page, perPage))
    fun loadQueryPage(query: String) = loadPage(api.getPostsForQuery(query, page, perPage))
    fun loadCategoryPage(category: LocalCategory) = loadPage(api.getPostsForCategory(category.id, page, perPage))

    private fun loadPage(loadingSingle: Single<Result<List<Post>>>): Single<List<LocalPost>> =
            Single.just(page > maximumNumberOfPostPages)
                    .flatMap {
                        if (it) Single.just(emptyList())
                        else loadingSingle
                                .doOnSuccess { page++ }
                                .doOnSuccess(::setMaxPages)
                                .map { it.response()?.body() }
                    }
                    .map { it.map(::LocalPost) }

    private fun <T> setMaxPages(result: Result<T>?) {
        result?.response()?.headers()?.values("X-WP-TotalPages")?.firstOrNull()?.toInt()?.let { maximumNumberOfPostPages = it }
    }
}

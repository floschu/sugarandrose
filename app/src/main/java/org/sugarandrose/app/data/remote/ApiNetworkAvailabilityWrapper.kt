package org.sugarandrose.app.data.remote

import android.content.Context
import io.reactivex.Single
import org.sugarandrose.app.data.model.remote.*
import org.sugarandrose.app.util.exceptions.NetworkUnavailableException
import org.sugarandrose.app.util.extensions.isNetworkAvailable
import retrofit2.adapter.rxjava2.Result

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class ApiNetworkAvailabilityWrapper(private val api: SugarAndRoseApi, private val context: Context) : SugarAndRoseApi {
    override fun getPostsFromDeepLink(query: String): Single<List<Post>> = loadWithNetworkUnavailabilityDetection(api.getPostsFromDeepLink(query))
    override fun getPost(id: Long): Single<Post> = loadWithNetworkUnavailabilityDetection(api.getPost(id))
    override fun getPosts(page: Int, perPage: Int): Single<Result<List<Post>>> = loadWithNetworkUnavailabilityDetection(api.getPosts(page, perPage))
    override fun getPostsForQuery(query: String, page: Int, perPage: Int): Single<Result<List<Post>>> = loadWithNetworkUnavailabilityDetection(api.getPostsForQuery(query, page, perPage))
    override fun getCategories(): Single<List<Category>> = loadWithNetworkUnavailabilityDetection(api.getCategories())
    override fun getPostsForCategory(id: Int, page: Int, perPage: Int): Single<Result<List<Post>>> = loadWithNetworkUnavailabilityDetection(api.getPostsForCategory(id, page, perPage))
    override fun getRoses(): Single<Roses> = loadWithNetworkUnavailabilityDetection(api.getRoses())
    override fun getMore(id: Long): Single<More> = loadWithNetworkUnavailabilityDetection(api.getMore(id))

    private fun <T> loadWithNetworkUnavailabilityDetection(loadingSingle: Single<T>): Single<T> =
            if (context.isNetworkAvailable) loadingSingle else Single.error(NetworkUnavailableException())
}
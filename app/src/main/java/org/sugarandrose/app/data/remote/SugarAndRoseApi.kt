package org.sugarandrose.app.data.remote

import io.reactivex.Single
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.data.model.remote.Post
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SugarAndRoseApi {

    //Single Items
    @GET("posts/{id}")
    fun getPost(@Path("id") id: Long): Single<Post>

    @GET("media/{id}")
    fun getMedia(@Path("id") id: Long): Single<Media>

    //Posts
    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsPage(@Query("page") page: Int = 1, @Query("per_page") perPage: Int = 5): Single<Result<List<Post>>>

    //Media
    @GET("media/?order=desc&?orderby=date_gmt")
    fun getMediaPage(@Query("page") page: Int = 1, @Query("per_page") perPage: Int = 5): Single<Result<List<Media>>>

    //Query
    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsForQuery(@Query("search") query: String, @Query("page") page: Int = 1, @Query("per_page") perPage: Int = 5): Single<Result<List<Post>>>

    //Categories
    @GET("categories/")
    fun getCategories(): Single<List<Category>>

    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsForCategory(@Query("categories") id: Int, @Query("page") page: Int = 1, @Query("per_page") perPage: Int = 5): Single<Result<List<Post>>>
}

const val TOTAL_PAGES_HEADER = "X-WP-TotalPages"
const val TOTAL_PAGES_DEFAULT = 10
fun <T> parseMaxPages(result: Result<T>?): Int =
        result?.response()?.headers()?.values(TOTAL_PAGES_HEADER)?.firstOrNull()?.toInt() ?: TOTAL_PAGES_DEFAULT
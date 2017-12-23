package org.sugarandrose.app.data.remote

import io.reactivex.Single
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.data.model.remote.Post
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val TOTAL_PAGES_HEADER = "X-WP-TotalPages"

interface SugarAndRoseApi {

    @GET("posts/{id}")
    fun getPost(@Path("id") id: Int): Single<Post>

    @GET("posts/?per_page=1&page=1")
    fun getNumberOfPages(): Single<Result<Void>>

    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPosts(@Query("page") page: Int): Single<List<Post>>

    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsForDay(@Query("after") after: String, @Query("before") before: String): Single<List<Post>>

    @GET("media/{id}")
    fun getMedia(@Path("id") id: Int): Single<Media>

    @GET("categories/")
    fun getCategories(): Single<List<Category>>

    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsForCategory(@Query("categories") id: Int): Single<List<Post>>
}
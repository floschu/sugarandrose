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

    @GET("media/{id}")
    fun getMedia(@Path("id") id: Int): Single<Media>

    //Posts
    @GET("posts/?per_page=1&page=1")
    fun getNumberOfPages(): Single<Result<Void>>
    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPosts(@Query("page") page: Int = 1): Single<List<Post>>

    //Query
    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsForQuery(@Query("search") query: String, @Query("page") page: Int = 1): Single<List<Post>>
    @GET("posts/?per_page=1&page=1")
    fun getNumberOfPagesForQuery(@Query("search") query: String): Single<Result<Void>>

    //Categories
    @GET("categories/")
    fun getCategories(): Single<List<Category>>
    @GET("posts/?order=desc&?orderby=date_gmt")
    fun getPostsForCategory(@Query("categories") id: Int, @Query("page") page: Int = 1): Single<List<Post>>
    @GET("posts/?per_page=1&page=1")
    fun getNumberOfPagesForCategory(@Query("categories") id: Int): Single<Result<Void>>
}
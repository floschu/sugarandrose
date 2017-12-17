package org.sugarandrose.app.data.remote

import io.reactivex.Single
import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.data.model.remote.Post
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SugarAndRoseApi {

    @GET("posts/")
    fun getPosts(@Query("page") page: Int = 1, @Query("per_page") perPage: Int = 10): Single<List<Post>>

    @GET("media/{id}")
    fun getMedia(@Path("id") id: Int): Single<Media>
}
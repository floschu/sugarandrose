package org.sugarandrose.app.data.remote

import io.reactivex.Single
import org.sugarandrose.app.data.model.remote.*
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SugarAndRoseApi {

    //Posts
    @GET("posts/{id}?fields=id,title,date,link,content,better_featured_image.source_url")
    fun getPost(@Path("id") id: Long): Single<Post>

    @GET("posts/?fields=id,title,date,link,better_featured_image.source_url&?order=desc&?orderby=date_gmt")
    fun getPosts(@Query("page") page: Int = 1, @Query("per_page") perPage: Int = 10): Single<Result<List<Post>>>

    @GET("posts/?fields=id")
    fun getPostsFromDeepLink(@Query("search") query: String): Single<List<Post>>



    //Query
    @GET("posts/?fields=id,title,date,link,better_featured_image.source_url&?order=desc&?orderby=date_gmt")
    fun getPostsForQuery(@Query("search") query: String, @Query("page") page: Int = 1, @Query("per_page") perPage: Int = 10): Single<Result<List<Post>>>



    //Categories
    @GET("categories/?fields=id,name,description,link,count,parent&?order=desc&?orderby=name&per_page=100")
    fun getCategories(): Single<List<Category>>

    @GET("posts/?fields=id,title,date,link,better_featured_image.source_url&?order=desc&?orderby=date_gmt")
    fun getPostsForCategory(@Query("categories") id: Int, @Query("page") page: Int = 1, @Query("per_page") perPage: Int = 10): Single<Result<List<Post>>>



    //Roses
    @GET("pages/6667/?fields=content")
    fun getRoses(): Single<Roses>



    //More
    @GET("pages/{id}?fields=id,link,better_featured_image.source_url,title")
    fun getMore(@Path("id") id: Long): Single<More>

}
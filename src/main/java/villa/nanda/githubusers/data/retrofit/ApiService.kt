package villa.nanda.githubusers.data.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import villa.nanda.githubusers.data.response.DetailResponse
import villa.nanda.githubusers.data.response.SearchResponse
import villa.nanda.githubusers.data.response.UserItem

interface ApiService {

    @GET("search/users")
    fun searchUser(@Query("q") q: String): Call<SearchResponse>

    @GET("users/{username}")
    fun getDetailUser(
        @Header("Authorization") auth: String,
        @Path("username") username: String
    ): Call<DetailResponse>

    @GET("users/{username}/followers")
    fun getFollowerUsers(
        @Header("Authorization") auth: String,
        @Path("username") username: String
    ): Call<List<UserItem>>

    @GET("users/{username}/following")
    fun getFollowingUsers(
        @Header("Authorization") auth: String,
        @Path("username") username: String
    ): Call<List<UserItem>>
}
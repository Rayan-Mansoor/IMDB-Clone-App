package com.example.erdtodatabase

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIcall {

    @GET("?")
    suspend fun getTopMovies(
        @Query("rapidapi-key")
        apiKey : String
    ) : Response<ApiResponse>

}
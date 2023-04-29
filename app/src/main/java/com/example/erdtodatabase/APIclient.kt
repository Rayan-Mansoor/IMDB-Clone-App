package com.example.erdtodatabase

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIclient {
    val base_URL = "https://imdb-top-100-movies.p.rapidapi.com/"

    fun getAPIinstance() : Retrofit {
        return Retrofit.Builder().baseUrl(base_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
}
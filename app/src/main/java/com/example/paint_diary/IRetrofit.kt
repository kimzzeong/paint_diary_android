package com.example.paint_diary

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofit {

    @GET(API.SEARCH_USER)
    fun searchUsers(@Query("query") userTerm:String) : Call<JsonElement>
}
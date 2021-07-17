package com.example.paint_diary

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//input
interface LoginService {
    @FormUrlEncoded
    @POST("retrofit_test.php")
    fun requestLogin(
        @Field("userid") userid:String,
        @Field("userpw") userpw:String
    ) : Call<Login> //output 정의
}
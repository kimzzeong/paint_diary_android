package com.example.paint_diary

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//input
interface IRetrofit {

    @FormUrlEncoded
    @POST("retrofit_test.php")
    fun requestLogin(
            @Field("userid") userid:String,
            @Field("userpw") userpw:String
    ) : Call<Login> //output 정의

    @FormUrlEncoded
    @POST("joinMembershipSave.php")
    fun requestJoinMembership(
            @Field("user_email") user_email:String,
            @Field("user_password") user_password:String,
            @Field("user_nickname") user_nickname:String
    ) : Call<JoinMembership> //output 정의
}


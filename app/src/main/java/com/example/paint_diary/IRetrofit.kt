package com.example.paint_diary

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//input
interface IRetrofit {

    //로그인
    @FormUrlEncoded
    @POST("loginProcess.php")
    fun requestLogin(
            @Field("user_email") user_email:String,
            @Field("user_password") user_password:String
    ) : Call<Login> //output 정의

    //회원가입
    @FormUrlEncoded
    @POST("joinMembershipSave.php")
    fun requestJoinMembership(
        @Field("user_email") user_email:String,
        @Field("user_password") user_password:String,
        @Field("user_nickname") user_nickname:String
    ) : Call<JoinMembership> //output 정의

    //마이페이지 프로필 불러오기
    @FormUrlEncoded
    @POST("callProfile.php")
    fun requestProfile(
        @Field("user_idx") user_idx:String
    ) : Call<Profile> //output 정의

    //회원탈퇴
    @FormUrlEncoded
    @POST("userWithdrawal.php")
    fun requestwithdrawal(
        @Field("user_idx") user_idx:String
    ) : Call<Withdrawal> //output 정의
}


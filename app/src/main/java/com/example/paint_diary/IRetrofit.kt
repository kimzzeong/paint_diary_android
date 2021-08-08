package com.example.paint_diary

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


//input
interface IRetrofit {

    //로그인
    @FormUrlEncoded
    @POST("loginProcess.php")
    fun requestLogin(
        @Field("user_email") user_email: String,
        @Field("user_password") user_password: String
    ) : Call<Login> //output 정의

    //회원가입
    @FormUrlEncoded
    @POST("joinMembershipSave.php")
    fun requestJoinMembership(
        @Field("user_email") user_email: String,
        @Field("user_password") user_password: String,
        @Field("user_nickname") user_nickname: String
    ) : Call<JoinMembership> //output 정의

    //마이페이지 프로필 불러오기
    @FormUrlEncoded
    @POST("callProfile.php")
    fun requestProfile(
        @Field("user_idx") user_idx: String
    ) : Call<Profile> //output 정의

    //회원탈퇴
    @FormUrlEncoded
    @POST("userWithdrawal.php")
    fun requestwithdrawal(
        @Field("user_idx") user_idx: String
    ) : Call<Withdrawal> //output 정의

    //프로필사진이 포함된 변경
    @Multipart
    @POST("profilePhotoChange.php")
    fun profilePhoto(
            @Part("user_idx") user_idx: RequestBody,
            @Part("user_nickname") user_nickname: RequestBody,
            @Part("user_introduction") user_introduction: RequestBody,
            @Part imageFile: MultipartBody.Part
    ): Call<ProfileChange>

    //프로필 사진이 포함되지 않은 변경
    @FormUrlEncoded
    @POST("profileChange.php")
    fun profileChange(
        @Field("user_idx") user_idx: String,
        @Field("user_nickname") user_nickname: String,
        @Field("user_introduction") user_introduction: String
    ): Call<ProfileChange>

    //프로필 사진이 기본사진
    @FormUrlEncoded
    @POST("profilePhotoChange.php")
    fun profileBasic(
        @Field("user_idx") user_idx: String,
        @Field("user_nickname") user_nickname: String,
        @Field("user_introduction") user_introduction: String,
        @Field("user_profile") user_profile:String
    ): Call<ProfileChange>

}


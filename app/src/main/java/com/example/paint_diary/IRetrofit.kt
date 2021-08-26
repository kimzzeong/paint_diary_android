package com.example.paint_diary

import com.example.paint_diary.Data.*
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
        @Field("user_idx") user_idx: Int
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
        @Field("user_profile") user_profile: String
    ): Call<ProfileChange>

    //다이어리 업로드
    @Multipart
    @POST("diaryupload.php")
    fun diaryUpload(
        @Part("user_idx") user_idx: RequestBody,
        @Part("diary_title") diary_title: RequestBody,
        @Part("diary_weather") diary_weather: RequestBody,
        @Part("diary_range") diary_range: RequestBody,
        @Part("diary_content") diary_content: RequestBody,
        @Part("diary_secret") diary_secret: RequestBody,
        @Part("diary_date") diary_date: RequestBody,
        @Part imageFile: MultipartBody.Part //diary_painting
    ): Call<DiaryUpload>

    //다이어리 불러오기
   // @FormUrlEncoded
    @POST("diary_request.php")
    fun requestDiary(
//        @Field("user_idx") user_idx: String
    ): Call<ArrayList<DiaryRequest>>

    //비밀번호 변경
    @GET("changePW.php")
    fun updatePW(
        @Query("user_idx") user_idx: String,
        @Query("password") password: String,
        @Query("change_password") change_password: String,
        @Query("change_password_check") change_password_check: String
    ) : Call<ChangePW> //output 정의Diary> //output 정의

    //다이어리 상세 불러오기
    @FormUrlEncoded
    @POST("diaryInfo.php")
    fun requestDiaryInfo(
        @Field("diary_idx") diary_idx: Int,
        @Field("diary_writer") diary_writer: Int

    ): Call<DiaryInfoPage>

    //좋아요 클릭 시 기능
    @GET("diary_like_process.php")
    fun requestContentLike(
        @Query("user_idx") user_idx: Int,
        @Query("diary_idx") diary_idx: Int,
        @Query("like_status") like_status: Int

        ) : Call<like_data> //output 정의Diary> //output 정의

    //좋아요 불러오기
    @GET("diary_like_request.php")
    fun requestLikeInfo(
            @Query("diary_idx") diary_idx: Int,
            @Query("user_idx") user_idx: Int
    ) : Call<like_data>

    //프로필 페이지 일기리스트 불러오기
    @FormUrlEncoded
    @POST("requestUserProfileDiary.php")
    fun requestUserProfileDiary(
            @Field("diary_wirter") diary_wirter: Int
    ): Call<ArrayList<DiaryList>>

    //다이어리 삭제
    @GET("removeDiary.php")
    fun requestRemoveDiary(
            @Query("diary_idx") diary_idx: Int
    ):Call<DiaryInfoPage>

    //다이어리 수정
    @FormUrlEncoded
    @POST("modifyDiary.php")
    fun requestUpdateDiary(
            @Field("diary_idx") diary_idx: Int,
            @Field("diary_title") diary_title: String,
            @Field("diary_weather") diary_weather: Int,
            @Field("diary_range") diary_range: Int,
            @Field("diary_content") diary_content: String,
            @Field("diary_date") diary_date: String,
            @Field("diary_secret") diary_secret: Int
    ):Call<DiaryInfoPage>

    //댓글 등록
    @FormUrlEncoded
    @POST("commentsUpload.php")
    fun sendComments(
            @Field("diary_idx") diary_idx: Int,
            @Field("comment_content") comment_content: String,
            @Field("comment_writer") comment_writer: Int,
            @Field("comment_secret") comment_secret: Int
    ):Call<CommentsList>

    //댓글 불러오기
    @FormUrlEncoded
    @POST("requestComments.php")
    fun requestComments(
            @Field("diary_idx") diary_idx: Int
    ): Call<ArrayList<CommentsList>>

    //댓글 갯수 불러오기
    @FormUrlEncoded
    @POST("requestCommentsCount.php")
    fun requestCommentsCount(
            @Field("diary_idx") diary_idx: Int
    ): Call<CommentsList>

    //댓글 삭제
    @GET("removeComments.php")
    fun requestRemoveComments(
            @Query("comment_idx") comment_idx: Int
    ):Call<CommentsList>

    //댓글 수정
    @GET("modifyComments.php")
    fun requestModifyComments(
            @Query("comment_idx") comment_idx: Int,
            @Query("comment_content") comment_content: String
    ):Call<CommentsList>
}


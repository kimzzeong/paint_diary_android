package com.example.paint_diary.Activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.paint_diary.DiaryInfoPage
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.example.paint_diary.like_data
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_diary_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DiaryInfoActivity : AppCompatActivity() {
    private var diary_idx: Int? = null
    private var user_profile: String = "http://3.36.52.195/profile/"
    private var diary_writer : Int? = null //user_nickname
    private var diary_painting : String = "http://3.36.52.195/diary/"
    private var diary_like : Int? = null
    private var diary_like_count : Int? = null
    private var user_idx : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_info)

        val intent = intent
        diary_idx = intent.getIntExtra("diary_idx",0)
        diary_writer = intent.getIntExtra("diary_wirter",0)
        val sharedPreferences = this.getSharedPreferences("user",0)
        var user_idx_str : String? = sharedPreferences?.getString("user_idx", "")
        user_idx = Integer.parseInt(user_idx_str)




        setSupportActionBar(diaryInfo_toolbar)
        supportActionBar?.title = "제목"

        requestDiaryInfo() //일기 상세글 정보 불러오기
        likeProcess()


        //좋아요
        diary_favorite.setOnClickListener {
            requestDiaryLike()
            diary_like_setting() // 좋아요 세팅

        }

        //댓글
        diary_comment.setOnClickListener {
            Toast.makeText(this,"댓글 클릭",Toast.LENGTH_SHORT).show()
        }


    }

    //좋아요 갯수 불러오기
    private fun likeProcess() {

        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var request_like = retrofit.create(IRetrofit::class.java)

        request_like.requestLikeInfo(diary_idx!!,user_idx!!).enqueue(object : Callback<like_data>{
            override fun onResponse(call: Call<like_data>, response: Response<like_data>) {
                val like = response.body()
                if(like != null){
                    diary_like = like.like_status
                    diary_like_count = like.like_count
                    diary_favorite_text.text = diary_like_count.toString()
                    diary_like_setting()

                    Toast.makeText(this@DiaryInfoActivity,""+diary_like,Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<like_data>, t: Throwable) {
                Toast.makeText(this@DiaryInfoActivity,"좋아요 불러오기 실패",Toast.LENGTH_SHORT).show()
            }

        })


    }

    //아이콘 클릭 시
    private fun requestDiaryLike() {

        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var request_like = retrofit.create(IRetrofit::class.java)
        request_like.requestContentLike(user_idx!!,diary_idx!!,diary_like!!).enqueue(object : Callback<like_data>{
            override fun onResponse(call: Call<like_data>, response: Response<like_data>) {
                val like = response.body()
                if (like != null) {
                    diary_like = like.like_status
                    Log.e("diary_like",diary_like.toString())
                    diary_favorite_text.text = like.like_count.toString()
                    Log.e("like_count",like.like_count.toString())
                    Toast.makeText(this@DiaryInfoActivity,"좋아요"+like.like_status,Toast.LENGTH_SHORT).show()
                    diary_like_setting()
                }
            }

            override fun onFailure(call: Call<like_data>, t: Throwable) {
                Toast.makeText(this@DiaryInfoActivity,"좋아요 실패.",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun diary_like_setting() {
        if(diary_like == 0){
            diary_favorite_icon.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }else{
            diary_favorite_icon.setImageResource(R.drawable.ic_baseline_favorite_24)
        }

    }

    private fun requestDiaryInfo() {
        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var requestDiaryInfo = retrofit.create(IRetrofit::class.java)

        requestDiaryInfo.requestDiaryInfo(diary_idx!!,diary_writer!!).enqueue(object : Callback<DiaryInfoPage> {
            override fun onResponse(call: Call<DiaryInfoPage>, response: Response<DiaryInfoPage>) {
                val diaryInfo = response.body()
                if (diaryInfo != null) {

                    //글쓴이 소개글
                    diaryInfo_intro.text = diaryInfo.user_intro

                    //글쓴이 닉네임
                    diaryInfo_nickname.text = diaryInfo.diary_nickname

                    //글쓴이 프로필, 프로필이 없으면 기본 사진
                    if(diaryInfo?.user_profile != null){
                        user_profile += diaryInfo.user_profile
                        Glide.with(applicationContext)
                            .load(user_profile)
                            .circleCrop()
                            .into(diaryInfo_profile)
                    }else{
                        diaryInfo_profile.setImageResource(R.drawable.basic_profile)
                    }

                    //일기 그림
                    diary_painting += diaryInfo.diary_painting
                    Glide.with(applicationContext)
                        .load(diary_painting)
                        .into(diaryInfo_painting)

                    //날짜
                    diaryInfo_date.text = diaryInfo.diary_date

                    //제목
                    diaryInfo_title.text = diaryInfo.diary_title

                    //날씨
                    when (diaryInfo.diary_weather) {
                        0 -> diaryInfo_weather.setImageResource(R.drawable.sun)
                        1 -> diaryInfo_weather.setImageResource(R.drawable.cloudy)
                        2 -> diaryInfo_weather.setImageResource(R.drawable.moon)
                        3 -> diaryInfo_weather.setImageResource(R.drawable.rainy)
                        4 -> diaryInfo_weather.setImageResource(R.drawable.snowflake)
                    }

                    //내용
                    diaryInfo_content.text = diaryInfo.diary_content

                    //내용 정렬
                    when (diaryInfo.diary_range) {
                        0 -> diaryInfo_content.gravity = Gravity.START
                        1 -> diaryInfo_content.gravity = Gravity.CENTER_HORIZONTAL
                        2 -> diaryInfo_content.gravity = Gravity.END
                    }
                }
            }

            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
                onBackPressed()
                Toast.makeText(this@DiaryInfoActivity,"다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            }

        })
    }
}

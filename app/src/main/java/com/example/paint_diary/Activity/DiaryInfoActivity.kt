package com.example.paint_diary.Activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.paint_diary.DiaryInfoPage
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_diary_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DiaryInfoActivity : AppCompatActivity() {
    var diary_idx: Int? = null
    var user_profile: String = "http://3.36.52.195/profile/"
    var diary_writer : Int? = null //user_nickname
    var diary_painting : String = "http://3.36.52.195/diary/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_info)

        val intent = intent
        diary_idx = intent.getIntExtra("diary_idx",0)
        diary_writer = intent.getIntExtra("diary_wirter",0)

        setSupportActionBar(diaryInfo_toolbar)
        supportActionBar?.title = "제목"

        diary_favorite.setOnClickListener {
            Toast.makeText(this,"풰이보릿 클릭",Toast.LENGTH_SHORT).show()
        }

        diary_comment.setOnClickListener {
            Toast.makeText(this,"댓글 클릭",Toast.LENGTH_SHORT).show()
        }
        requestDiaryInfo() //일기 상세글 정보 불러오기


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
                    //툴바에 글쓴이 정보
                    diaryInfo_intro.text = diaryInfo.user_intro
                    diaryInfo_nickname.text = diaryInfo.diary_nickname

                    //본문 내용 일기 정보
                    diary_painting += diaryInfo.diary_painting
                    Glide.with(applicationContext)
                        .load(diary_painting)
                        .into(diaryInfo_painting)
                    diaryInfo_date.text = diaryInfo.diary_date
                    diaryInfo_title.text = diaryInfo.diary_title
                    //날씨
                    when (diaryInfo.diary_weather) {
                        0 -> diaryInfo_weather.setImageResource(R.drawable.sun)
                        1 -> diaryInfo_weather.setImageResource(R.drawable.cloudy)
                        2 -> diaryInfo_weather.setImageResource(R.drawable.moon)
                        3 -> diaryInfo_weather.setImageResource(R.drawable.rainy)
                        4 -> diaryInfo_weather.setImageResource(R.drawable.snowflake)
                    }
                    diaryInfo_content.text = diaryInfo.diary_content
                    //정렬
                    when (diaryInfo.diary_range) {
                        0 -> diaryInfo_content.gravity = Gravity.START
                        1 -> diaryInfo_content.gravity = Gravity.CENTER_HORIZONTAL
                        2 -> diaryInfo_content.gravity = Gravity.END
                    }
                }
                //프로필이 없으면 기본 프로필
                if(diaryInfo?.user_profile != null){
                    user_profile += diaryInfo.user_profile
                    Glide.with(applicationContext)
                        .load(user_profile)
                        .circleCrop()
                        .into(diaryInfo_profile)
                }else{
                    diaryInfo_profile.setImageResource(R.drawable.basic_profile)
                }

            }

            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
                onBackPressed()
                Toast.makeText(this@DiaryInfoActivity,"다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            }

        })
    }
}

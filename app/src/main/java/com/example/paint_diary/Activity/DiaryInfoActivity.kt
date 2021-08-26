package com.example.paint_diary.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.DiaryInfoPage
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.example.paint_diary.Data.like_data
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_diary_info.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile_modify.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DiaryInfoActivity : AppCompatActivity() {
    private var diary_idx: Int? = null
    private var diary_writer : Int? = null //user_nickname
    private var diary_like : Int? = null
    private var diary_like_count : Int? = null
    private var user_idx : Int? = null


    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

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

        //로그인 한 유저가 글쓴이가 아닐 경우 수정/삭제 팝업 안보여주기
        if(user_idx != diary_writer){
            diaryInfo_more_menu.visibility = View.INVISIBLE
        }


        //좋아요
        diary_favorite.setOnClickListener {
            requestDiaryLike()
            diary_like_setting() // 좋아요 세팅

        }

        //댓글
        diary_comment.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("diary_idx",diary_idx)
            startActivity(intent)
        }

        //프로필 사진 클릭 시 유저프로필 액티비티로 이동
        diaryInfo_profile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("diary_writer",diary_writer)
            startActivity(intent)
        }

        //새로고침
        diaryInfo_refresh.setOnRefreshListener {
            requestDiaryInfo() //일기 상세글 정보 불러오기
            likeProcess()
            diaryInfo_refresh.isRefreshing = false
        }

        //팝업메뉴 클릭시 수정/삭제
        diaryInfo_more_menu.setOnClickListener {
            val dairyPopup = PopupMenu(this, diaryInfo_more_menu)
            menuInflater?.inflate(R.menu.diary_modify_menu, dairyPopup.menu)

            dairyPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.diary_modify -> {
                        //updateDiary()
                        val intent = Intent(this, DiaryActivity::class.java)
                        intent.putExtra("update","update") // 새글쓰는게 아니라 글 업데이트임을 보여줌
                        intent.putExtra("diary_idx",diary_idx)
                        intent.putExtra("diary_writer",diary_writer)
                        startActivity(intent)
                    }

                    R.id.diary_remove -> {
                        removeDiary()
                    }

                }
                false
            }

            dairyPopup.show()
        }
    }

    private fun removeDiary() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("정말 삭제하시겠습니까?")
        dialog.setCancelable(false);
        dialog.setPositiveButton("네"){ dialog, id ->

            requestDiaryRemove()
        }
        dialog.setNegativeButton("아니오"){ dialog, id ->

        }
        dialog.show()
    }


    private fun requestDiaryRemove() {
        var requestDiaryRemove = retrofit.create(IRetrofit::class.java)
        requestDiaryRemove.requestRemoveDiary(diary_idx!!).enqueue(object : Callback<DiaryInfoPage>{
            override fun onResponse(call: Call<DiaryInfoPage>, response: Response<DiaryInfoPage>) {
                val diary = response.body()
                Toast.makeText(this@DiaryInfoActivity,diary?.message,Toast.LENGTH_SHORT).show()
                finish()

            }

            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
            }

        })
    }

    //좋아요 갯수 불러오기
    private fun likeProcess() {

        var request_like = retrofit.create(IRetrofit::class.java)

        request_like.requestLikeInfo(diary_idx!!,user_idx!!).enqueue(object : Callback<like_data>{
            override fun onResponse(call: Call<like_data>, response: Response<like_data>) {
                val like = response.body()
                if(like != null){
                    diary_like = like.like_status
                    diary_like_count = like.like_count
                    diary_favorite_text.text = diary_like_count.toString()
                    diary_comment_text.text = like.comments_count.toString()

                    diary_like_setting()

                }
            }

            override fun onFailure(call: Call<like_data>, t: Throwable) {
                Toast.makeText(this@DiaryInfoActivity,"좋아요 불러오기 실패",Toast.LENGTH_SHORT).show()
            }

        })
    }

    //좋아요 클릭 시
    private fun requestDiaryLike() {

        var request_like = retrofit.create(IRetrofit::class.java)
        request_like.requestContentLike(user_idx!!,diary_idx!!,diary_like!!).enqueue(object : Callback<like_data>{
            override fun onResponse(call: Call<like_data>, response: Response<like_data>) {
                val like = response.body()
                if (like != null) {
                    likeProcess()
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
            diary_favorite_icon.setColorFilter(Color.rgb(116,113,111))
        }else{
            diary_favorite_icon.setImageResource(R.drawable.ic_baseline_favorite_24)
            diary_favorite_icon.setColorFilter(Color.rgb(236,86,43))
        }

    }

    private fun requestDiaryInfo() {

        var requestDiaryInfo = retrofit.create(IRetrofit::class.java)

        requestDiaryInfo.requestDiaryInfo(diary_idx!!,diary_writer!!).enqueue(object : Callback<DiaryInfoPage> {
            override fun onResponse(call: Call<DiaryInfoPage>, response: Response<DiaryInfoPage>) {
                val diaryInfo = response.body()
                if (diaryInfo != null) {

                    if(diaryInfo.diary_status == 1){
                        Toast.makeText(this@DiaryInfoActivity,"삭제된 게시물입니다.",Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }else{
                        var user_profile: String = "http://3.36.52.195/profile/"
                        var diary_painting : String = "http://3.36.52.195/diary/"

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
                        diaryInfo_weather.setColorFilter(Color.rgb(230,220,211))

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
            }

            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
                onBackPressed()
                Toast.makeText(this@DiaryInfoActivity,"다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        likeProcess()
    }
}

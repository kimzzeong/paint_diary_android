package com.example.paint_diary.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
    private var diary_writer : Int? = null
    private var diary_like : Int? = null
    private var diary_like_count : Int? = null
    private var user_idx : Int? = null

    companion object {
        var diaryInfoActivity: Activity? = null
    }


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
        diary_writer = intent.getIntExtra("diary_writer",0)
        diaryInfoActivity = this

        setSupportActionBar(diaryInfo_toolbar)
        supportActionBar?.title = "??????"

        requestDiaryInfo() //?????? ????????? ?????? ????????????



        //?????????
        diary_favorite.setOnClickListener {
            requestDiaryLike()
            diary_like_setting() // ????????? ??????

        }

        //??????
        diary_comment.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("diary_idx",diary_idx)
            intent.putExtra("diary_writer",diary_writer)
            startActivity(intent)
        }

        //????????? ?????? ?????? ??? ??????????????? ??????????????? ??????
        diaryInfo_profile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("diary_writer",diary_writer)
            startActivity(intent)
        }

        //????????????
        diaryInfo_refresh.setOnRefreshListener {
            requestDiaryInfo() //?????? ????????? ?????? ????????????
            likeProcess()
            diaryInfo_refresh.isRefreshing = false
        }

        //???????????? ????????? ??????/??????
        diaryInfo_more_menu.setOnClickListener {
            val dairyPopup = PopupMenu(this, diaryInfo_more_menu)
            menuInflater?.inflate(R.menu.diary_modify_menu, dairyPopup.menu)

            dairyPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.diary_modify -> {
                        //updateDiary()
                        val intent = Intent(this, DiaryActivity::class.java)
                        intent.putExtra("update","update") // ??????????????? ????????? ??? ?????????????????? ?????????
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
        dialog.setMessage("?????? ?????????????????????????")
        dialog.setCancelable(false);
        dialog.setPositiveButton("???"){ dialog, id ->

            requestDiaryRemove()
        }
        dialog.setNegativeButton("?????????"){ dialog, id ->

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

    //????????? ?????? ????????????
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
                Toast.makeText(this@DiaryInfoActivity,"????????? ???????????? ??????",Toast.LENGTH_SHORT).show()
            }

        })
    }

    //????????? ?????? ???
    private fun requestDiaryLike() {
        if(user_idx != 0){
            var request_like = retrofit.create(IRetrofit::class.java)
            request_like.requestContentLike(user_idx!!,diary_idx!!,diary_like!!).enqueue(object : Callback<like_data>{
                override fun onResponse(call: Call<like_data>, response: Response<like_data>) {
                    val like = response.body()
                    if (like != null) {
                        likeProcess()
                    }
                }

                override fun onFailure(call: Call<like_data>, t: Throwable) {
                    Toast.makeText(this@DiaryInfoActivity,"????????? ??????.",Toast.LENGTH_SHORT).show()
                }

            })
        }else{
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("???????????? ????????? ?????? ????????? ??????????????????.\n?????? ?????? ????????????????????????????")
            dialog.setCancelable(false);
            dialog.setPositiveButton("???"){ dialog, id ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            dialog.setNegativeButton("?????????"){ dialog, id ->

            }
            dialog.show()
        }
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
                        Toast.makeText(this@DiaryInfoActivity,"????????? ??????????????????.",Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }else{
                        var user_profile: String = "http://3.36.52.195/profile/"
                        var diary_painting : String = "http://3.36.52.195/diary/"

                        //????????? ?????????
                        diaryInfo_intro.text = diaryInfo.user_intro

                        //????????? ?????????
                        diaryInfo_nickname.text = diaryInfo.diary_nickname

                        //????????? ?????????, ???????????? ????????? ?????? ??????
                        if(diaryInfo?.user_profile != null){
                            user_profile += diaryInfo.user_profile
                            Glide.with(applicationContext)
                                .load(user_profile)
                                .circleCrop()
                                .into(diaryInfo_profile)
                        }else{
                            diaryInfo_profile.setImageResource(R.drawable.basic_profile)
                        }

                        //?????? ??????
                        diary_painting += diaryInfo.diary_painting
                        Glide.with(applicationContext)
                            .load(diary_painting)
                            .into(diaryInfo_painting)

                        //??????
                        diaryInfo_date.text = diaryInfo.diary_date

                        //??????
                        diaryInfo_title.text = diaryInfo.diary_title

                        //??????
                        when (diaryInfo.diary_weather) {
                            0 -> diaryInfo_weather.setImageResource(R.drawable.sun)
                            1 -> diaryInfo_weather.setImageResource(R.drawable.cloudy)
                            2 -> diaryInfo_weather.setImageResource(R.drawable.moon)
                            3 -> diaryInfo_weather.setImageResource(R.drawable.rainy)
                            4 -> diaryInfo_weather.setImageResource(R.drawable.snowflake)
                        }
                        diaryInfo_weather.setColorFilter(Color.rgb(230,220,211))

                        //??????
                        diaryInfo_content.text = diaryInfo.diary_content

                        //?????? ??????
                        when (diaryInfo.diary_range) {
                            0 -> diaryInfo_content.gravity = Gravity.START
                            1 -> diaryInfo_content.gravity = Gravity.CENTER_HORIZONTAL
                            2 -> diaryInfo_content.gravity = Gravity.END
                        }


                        //????????? ??? ????????? ???????????? ?????? ?????? ??????/?????? ?????? ???????????????
                        if(user_idx != diary_writer){
                            diaryInfo_more_menu.visibility = View.INVISIBLE
                        }else{
                            diaryInfo_more_menu.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
                onBackPressed()
                Toast.makeText(this@DiaryInfoActivity,"?????? ??????????????????.",Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val intent = intent
        diary_idx = intent.getIntExtra("diary_idx",0)
        diary_writer = intent.getIntExtra("diary_writer",0)

        val sharedPreferences = this.getSharedPreferences("user",0)
        var user_idx_str : String? = sharedPreferences?.getString("user_idx", "0")
            user_idx = Integer.parseInt(user_idx_str)
        requestDiaryInfo()
        likeProcess()
    }
}

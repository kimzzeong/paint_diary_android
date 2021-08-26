package com.example.paint_diary.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.*
import com.example.paint_diary.Adapter.UserProfileDiaryListAdapter
import com.example.paint_diary.Data.DiaryList
import com.example.paint_diary.Data.Profile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileActivity : AppCompatActivity() {
    private var diary_writer: Int? = null
    lateinit var userProfileDiaryListAdapter: UserProfileDiaryListAdapter
    var profile_photo : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val intent = intent
        diary_writer = intent.getIntExtra("diary_writer",0)
        userProfileDiaryListAdapter = UserProfileDiaryListAdapter()
        requestProfile()
        requestDiary()
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        user_profile_diaryList.layoutManager = layoutManager

        userProfileDiaryListAdapter.setItemClickListener(object : UserProfileDiaryListAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, diary_idx: Int, diary_wirter: Int) {
                val intent = Intent(this@UserProfileActivity, DiaryInfoActivity::class.java)
                intent.putExtra("diary_idx",diary_idx)
                intent.putExtra("diary_wirter",diary_wirter)
                Log.e("click",diary_idx.toString())
                startActivity(intent)
            }

        })

    }

    private fun requestProfile() {
        var gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        var user_profile = retrofit.create(IRetrofit::class.java)
        if (diary_writer != null) {
            user_profile.requestProfile(diary_writer!!).enqueue(object: Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {

                    var profile = response.body()
                    user_profile_intro.text = profile?.user_introduction
                    user_profile_nickname.text = profile?.user_nickname
                    if(profile?.user_profile == null){
                        user_profile_photo.setImageResource(R.drawable.basic_profile)
                        profile_photo = null
                    }else{
                        var uri : Uri = Uri.parse(profile?.user_profile)
                        profile_photo = "http://3.36.52.195/profile/"+uri
                        //profile_photo = "/data/user/0/com.example.paint_diary/cache/"+uri
                        Glide.with(this@UserProfileActivity) // context
                                .load(profile_photo) // 이미지 url
                                .into(user_profile_photo); // 붙일 imageView
                        // mypage_profile_photo.setImageResource(R.drawable.ic_baseline_add_24)
                    }



                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("실패",t.localizedMessage)
                }

            })
        }
    }

    private fun requestDiary() {
//        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
//        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var diary_request = retrofit.create(IRetrofit::class.java)

        diary_request.requestUserProfileDiary(diary_writer!!).enqueue(object : Callback<ArrayList<DiaryList>> {
            override fun onResponse(call: Call<ArrayList<DiaryList>>, response: Response<ArrayList<DiaryList>>
            ) {

                var diary = response.body()!!
                user_profile_diaryList.adapter = userProfileDiaryListAdapter

                userProfileDiaryListAdapter.diary_List = diary
                userProfileDiaryListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ArrayList<DiaryList>>, t: Throwable) {
                Log.e("실패", t.localizedMessage)
            }

        })
        //diary_request.requestProfile()

    }
}
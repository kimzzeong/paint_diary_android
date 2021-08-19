package com.example.paint_diary.Activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_diary.Adapter.DiaryRecyclerviewAdapter
import com.example.paint_diary.Adapter.UserProfileDiaryListAdapter
import com.example.paint_diary.DiaryList
import com.example.paint_diary.DiaryRequest
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.diary_list
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileActivity : AppCompatActivity() {
    private var diary_writer: Int? = null
    lateinit var userProfileDiaryListAdapter: UserProfileDiaryListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val intent = intent
        diary_writer = intent.getIntExtra("diary_writer",0)
        userProfileDiaryListAdapter = UserProfileDiaryListAdapter()
        requestDiary()
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        user_profile_diaryList.layoutManager = layoutManager

    }

    private fun requestDiary() {
//        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
//        var user_idx : String? = sharedPreferences?.getString("user_idx", "")
        Toast.makeText(this,""+diary_writer,Toast.LENGTH_SHORT).show()

        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var diary_request = retrofit.create(IRetrofit::class.java)

        diary_request.requestUserProfile(diary_writer!!).enqueue(object : Callback<ArrayList<DiaryList>> {
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
    }
}
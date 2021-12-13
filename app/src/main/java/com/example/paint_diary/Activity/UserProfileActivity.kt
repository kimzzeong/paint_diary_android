package com.example.paint_diary.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.*
import com.example.paint_diary.Adapter.UserProfileDiaryListAdapter
import com.example.paint_diary.Data.Chat
import com.example.paint_diary.Data.ChatRoom
import com.example.paint_diary.Data.DiaryList
import com.example.paint_diary.Data.Profile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_chat.*
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
    var room_idx = 0

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    var user_idx : String? = null
    var user_nickname : String? = null
    var diary_writer_nickname : String? = null

    var my_chat_list : ArrayList<ChatRoom>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        user_idx = sharedPreferences?.getString("user_idx", "")
        user_nickname = sharedPreferences?.getString("user_nickname", "")

        val intent = intent
        diary_writer = intent.getIntExtra("diary_writer",0)
        Log.e("diary_writer",""+diary_writer)
        Log.e("user_idx",""+user_idx)
        userProfileDiaryListAdapter = UserProfileDiaryListAdapter()
        requestProfile()
        requestDiary()

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        user_profile_diaryList.layoutManager = layoutManager

        userProfileDiaryListAdapter.setItemClickListener(object : UserProfileDiaryListAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, diary_idx: Int, diary_wirter: Int) {
                val intent = Intent(this@UserProfileActivity, DiaryInfoActivity::class.java)
                intent.putExtra("diary_idx",diary_idx)
                intent.putExtra("diary_writer",diary_wirter)
                Log.e("click",diary_idx.toString())
                startActivity(intent)
            }

        })

        //현재 로그인 중인 유저와 사용자 프로필의 유저가 같을 경우 채팅하기 버튼 안보이게 함
        if(user_idx.equals(""+diary_writer)){
            user_profile_chat_btn.visibility = View.GONE
            user_profile_follow.visibility = View.GONE
        }else{
            user_profile_chat_btn.visibility = View.VISIBLE
            user_profile_follow.visibility = View.VISIBLE
        }

        //채팅하기 버튼 클릭 시 채팅방 생성
        user_profile_chat_btn.setOnClickListener {
            requesstCreateChatRoom(user_idx+","+diary_writer)
            val intent = Intent(this@UserProfileActivity, ChatActivity::class.java)
            intent.putExtra("room_idx",room_idx)
            startActivity(intent)
        }

    }

    private fun requesstCreateChatRoom(users_idx : String){

        var create_room = retrofit.create(IRetrofit::class.java)
        create_room.requestChatRoomCreate(users_idx).enqueue(object : Callback<ChatRoom>{
            override fun onResponse(call: Call<ChatRoom>, response: Response<ChatRoom>) {
                var room = response.body()
                if (room != null) {
                    room_idx = room.room_idx
                        Toast.makeText(this@UserProfileActivity,room.room_idx.toString(),Toast.LENGTH_SHORT).show()
                }
                //room_idx = room?.room_idx!!

            }

            override fun onFailure(call: Call<ChatRoom>, t: Throwable) {

            }


        })
    }

    //채팅방 전체 리스트 불러오기
    private fun requestChatRoom() : ArrayList<ChatRoom> {
        val my_room_list = java.util.ArrayList<ChatRoom>()
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")
        var room = ArrayList<ChatRoom>()
        //loaderLayout.visibility = View.VISIBLE
        var chatRoom_request = retrofit.create(IRetrofit::class.java)

        chatRoom_request.requestChatRoom(user_idx!!).enqueue(object : Callback<java.util.ArrayList<ChatRoom>>{
            override fun onResponse(call: Call<java.util.ArrayList<ChatRoom>>, response: Response<java.util.ArrayList<ChatRoom>>) {
                room = response.body()!!

                //코틀린의 for문. room list이고 i가 ChatRoom 객체임
                for(i in room){
                    if(i.room_user.contains(user_idx)){

                        Log.e("room_user",i.room_user)
                        my_room_list.add(i)
                    }
                }
            }

            override fun onFailure(call: Call<java.util.ArrayList<ChatRoom>>, t: Throwable) {
                Log.e("레트로핏 에러","채팅")
            }

        })

        return my_room_list

    }

    private fun requestProfile() {


        var user_profile = retrofit.create(IRetrofit::class.java)
        if (diary_writer != null) {
            user_profile.requestProfile(diary_writer!!).enqueue(object: Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {

                    var profile = response.body()
                    user_profile_intro.text = profile?.user_introduction
                    user_profile_nickname.text = profile?.user_nickname
                    diary_writer_nickname = profile?.user_nickname
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

    }
}
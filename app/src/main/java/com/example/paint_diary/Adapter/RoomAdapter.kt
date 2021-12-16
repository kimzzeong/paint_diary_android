package com.example.paint_diary.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.Chat
import com.example.paint_diary.Data.ChatRoom
import com.example.paint_diary.Data.Profile
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_user_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    var roomList = mutableListOf<ChatRoom>()
    var context : Context? = null
    var user_idx : Int = 0 //유저 고유 번호

    var room_profile : Int = 0
    var profile_photo : String = ""

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    var retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    var user_nickname : String = "" //유저 닉네임
    interface ItemClickListener {
        fun onClick(view: View, position: Int, room_idx: Int, room_user1: String, room_user2 : String, profile_photo : String)
    }


    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val sharedPreferences = context?.getSharedPreferences("user",0)
        user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", "0"))
        user_nickname = sharedPreferences?.getString("user_nickname", "0").toString()
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_room, parent, false)
        Log.e("onCreateViewHolder","onCreateViewHolder")
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.e("onBindViewHolder","onBindViewHolder")
        holder.bind(roomList.get(position))

        var user_split = roomList.get(position).room_user.split(",")
        Log.e("user_split",user_split[0])
        Log.e("user_split",user_split[1])


        for(i in user_split){
          if(!i.equals(user_idx)){
              room_profile = i.toInt()
              break
          }
        }


        //채팅방 이미지(나와 채팅하는 상대방 프로필 이미지
        if(roomList.get(position).room_photo == null){

            holder.chat_profile.setImageResource(R.drawable.basic_profile)
            profile_photo = ""
        }else{
            var uri : Uri = Uri.parse(roomList.get(position).room_photo)
            profile_photo = "http://3.36.52.195/profile/"+uri
            //profile_photo = "/data/user/0/com.example.paint_diary/cache/"+uri
            Glide.with(context!!) // context
                .load(profile_photo) // 이미지 url
                .into(holder.chat_profile); // 붙일 imageView
        }



        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it,position,roomList.get(position).room_idx,user_split[0],user_split[1],profile_photo)

            Log.e("Adapter - profile_photo",profile_photo)
        }


    }

    override fun getItemCount(): Int {
        return roomList.size
    }


    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){

        var chat_profile : ImageView = itemView.findViewById(R.id.chat_profile)
        var chat_nickname : TextView = itemView.findViewById(R.id.chat_nickname)
        var chat_datetime : TextView = itemView.findViewById(R.id.chat_datetime)
        var chat_content : TextView = itemView.findViewById(R.id.chat_content)


        fun bind(item: ChatRoom){
            chat_nickname.text = item.room_name
            chat_datetime.text = item.room_datetime
            chat_content.text = item.message //나중에 jdbc 연결했을 때 마지막 메세지 보여주기
        }

    }

}
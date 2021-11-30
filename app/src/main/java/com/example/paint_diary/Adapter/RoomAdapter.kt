package com.example.paint_diary.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_diary.Data.ChatRoom
import com.example.paint_diary.R

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    var roomList = mutableListOf<ChatRoom>()
    var context : Context? = null
    var user_idx : Int = 0 //유저 고유 번호


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val sharedPreferences = context?.getSharedPreferences("user",0)
        user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", "0"))
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_room, parent, false)
        Log.e("onCreateViewHolder","onCreateViewHolder")
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.e("onBindViewHolder","onBindViewHolder")
        holder.bind(roomList.get(position))


    }

    override fun getItemCount(): Int {
        Log.e("getItemCount","getItemCount")
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
            chat_content.text = "일단 비워져있으요"
//            var uriToString : String = item.room_profilePhoto
//            if(uriToString != null){
//
//                var uri : Uri = Uri.parse(uriToString)
//                var uri_diary = "http://3.36.52.195/profile/"+uri
//                Glide.with(itemView).load(uri_diary).into(chat_profile)
//            }else{
                chat_profile.setImageResource(R.drawable.basic_profile)
           // }
        }

    }

}
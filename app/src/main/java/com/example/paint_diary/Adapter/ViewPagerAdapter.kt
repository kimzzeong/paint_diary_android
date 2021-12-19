package com.example.paint_diary.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.ChatRoom
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.chat_image_viewpager_item.view.*

class ViewPagerAdapter(imageList : ArrayList<Int>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    var item = imageList
    var context : Context? = null

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var chat_image : ImageView = itemView.findViewById(R.id.chat_image)


        fun bind(item: Int){

            Glide.with(context!!).load(item).into(chat_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        context = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_image_viewpager_item, parent, false)
        Log.e("onCreateViewHolder","onCreateViewHolder")
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item.get(position))
    }

    override fun getItemCount(): Int {
        return item.size
    }
}
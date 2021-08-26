package com.example.paint_diary.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.DiaryRequest
import com.example.paint_diary.R

class DiaryRecyclerviewAdapter:RecyclerView.Adapter<DiaryRecyclerviewAdapter.ViewHolder>(){

    var diaryList: ArrayList<DiaryRequest>? = null

    interface ItemClickListener {
        fun onClick(view: View, position: Int, diary_idx :Int, diary_wirter : Int)
    }


    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(diaryList!!.get(position))
        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position,diaryList!!.get(position).diary_idx,diaryList!!.get(position).diary_writer)
        }
    }

    override fun getItemCount(): Int {
        return diaryList!!.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var diaryImage : ImageView = itemView.findViewById(R.id.diary_image)
        var diaryWriter : TextView = itemView.findViewById(R.id.diary_writer)
        var diaryTitle : TextView = itemView.findViewById(R.id.diary_title)


        fun bind(item: DiaryRequest) {
            diaryTitle.text = item.diary_title
            diaryWriter.text = item.user_nickname
            var uriToString : String = item.diary_painting
            var uri : Uri = Uri.parse(uriToString)
            var uri_diary = "http://3.36.52.195/diary/"+uri
            Glide.with(itemView).load(uri_diary).into(diaryImage)

        }
    }

}
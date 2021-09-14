package com.example.paint_diary.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.CommentsList
import com.example.paint_diary.Data.DiaryRequest
import com.example.paint_diary.R
import java.util.*


class DiaryRecyclerviewAdapter:RecyclerView.Adapter<DiaryRecyclerviewAdapter.ViewHolder>(){

    var diaryList: ArrayList<DiaryRequest>? = null
    var context : Context? = null
    var user_idx : Int = 0 //유저 고유 번호

    interface ItemClickListener {
        fun onClick(view: View, position: Int, diary_idx: Int, diary_wirter: Int)
    }


    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val sharedPreferences = context?.getSharedPreferences("user",0)
        user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", "0"))
        val v = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val diaryRequest: DiaryRequest = diaryList!!.get(position)

        holder.bind(diaryList!!.get(position))
            holder.itemView.setOnClickListener {
                if(diaryRequest.diary_secret == 1 && diaryRequest.diary_writer == user_idx){
                itemClickListner.onClick(it, position, diaryList!!.get(position).diary_idx, diaryList!!.get(position).diary_writer)
            }else if(diaryRequest.diary_secret == 1 && diaryRequest.diary_writer != user_idx){
                    Toast.makeText(context,"비밀글입니다.",Toast.LENGTH_SHORT).show()
            }else{
                itemClickListner.onClick(it, position, diaryList!!.get(position).diary_idx, diaryList!!.get(position).diary_writer)
                }
        }
    }

    override fun getItemCount(): Int {
        return diaryList!!.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var diaryImage : ImageView = itemView.findViewById(R.id.diary_image)
        var diaryWriter : TextView = itemView.findViewById(R.id.diary_writer)
        var diaryTitle : TextView = itemView.findViewById(R.id.diary_title)
        var favorite_text : TextView = itemView.findViewById(R.id.favorite_text)
        var comments_text : TextView = itemView.findViewById(R.id.comments_text)
        var diary_date : TextView = itemView.findViewById(R.id.diary_date)
        var diary_lock : ImageView = itemView.findViewById(R.id.diary_lock)

        fun bind(item: DiaryRequest) {
            //비밀글이고 비밀글 글쓴이와 현재 로그인 중인 유저가 다를 때
            if(item.diary_secret == 1 && item.diary_writer != user_idx){
                diaryTitle.text = "비밀글입니다."
                diaryWriter.text = ""
                diary_date.text = ""
                favorite_text.text = item.diary_like_count.toString()
                comments_text.text= item.diary_comment_count.toString()
                Glide.with(itemView).load(R.drawable.secret).into(diaryImage)
                diary_lock.visibility = View.INVISIBLE
                //비밀글이고 비밀글 글쓴이와 현재 로그인 중인 유저가 같을 때
            }else if(item.diary_secret == 1 && item.diary_writer == user_idx){
                diaryTitle.text = item.diary_title
                diaryWriter.text = item.user_nickname
                favorite_text.text = item.diary_like_count.toString()
                comments_text.text= item.diary_comment_count.toString()
                diary_date.text = item.diary_date
                diary_lock.visibility = View.VISIBLE

                var uriToString : String = item.diary_painting
                var uri : Uri = Uri.parse(uriToString)
                var uri_diary = "http://3.36.52.195/diary/"+uri
                Glide.with(itemView).load(uri_diary).into(diaryImage)

                //그 외
            }else{
                diaryTitle.text = item.diary_title
                diaryWriter.text = item.user_nickname
                favorite_text.text = item.diary_like_count.toString()
                comments_text.text= item.diary_comment_count.toString()
                diary_date.text = item.diary_date
                diary_lock.visibility = View.INVISIBLE

                var uriToString : String = item.diary_painting
                var uri : Uri = Uri.parse(uriToString)
                var uri_diary = "http://3.36.52.195/diary/"+uri
                Glide.with(itemView).load(uri_diary).into(diaryImage)
            }
        }
    }

}
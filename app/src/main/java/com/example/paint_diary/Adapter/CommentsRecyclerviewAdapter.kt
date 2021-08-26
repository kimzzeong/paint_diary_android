package com.example.paint_diary.Adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Activity.CommentsActivity
import com.example.paint_diary.CommentsList
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CommentsRecyclerviewAdapter:RecyclerView.Adapter<CommentsRecyclerviewAdapter.ViewHolder>(){

    var commentsList: ArrayList<CommentsList>? = null
    var mContext : Context? = null
    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }


    private lateinit var itemClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comments_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentItem: CommentsList = commentsList!!.get(position)
        holder.bind(commentsList!!.get(position))
        holder.itemView.setOnClickListener {
            itemClickListner.onClick(it, position)
        }

        val sharedPreferences = mContext?.getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", ""))
        if(commentItem.comment_writer == user_idx){
            holder.itemView.setBackgroundColor(Color.parseColor("#24EFCAA6"))
            holder.setComments.visibility = View.VISIBLE
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.setComments.visibility = View.INVISIBLE
        }

        holder.setComments.setOnClickListener {
            val commentsPopup = PopupMenu(mContext!!, holder.setComments)
            commentsPopup.menuInflater?.inflate(R.menu.diary_modify_menu, commentsPopup.menu)

            commentsPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.diary_modify -> {
                        Toast.makeText(mContext, "수정", Toast.LENGTH_SHORT).show()
                    }

                    R.id.diary_remove -> {
                        val dialog = AlertDialog.Builder(mContext!!)
                        dialog.setMessage("정말 삭제하시겠습니까?")
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("네") { dialog, id ->

                            requestCommentsRemove(commentItem.comment_idx)
                            //(mContext as CommentsActivity).requestCommentList()

                        }
                        dialog.setNegativeButton("아니오") { dialog, id ->

                        }
                        dialog.show()
                    }

                }
                false
            }

            commentsPopup.show()
        }
    }

    private fun requestCommentsRemove(comment_idx: Int) {
        var requestDiaryRemove = retrofit.create(IRetrofit::class.java)
        requestDiaryRemove.requestRemoveComments(comment_idx!!).enqueue(object : Callback<CommentsList> {
            override fun onResponse(call: Call<CommentsList>, response: Response<CommentsList>) {
                val comments = response.body()
                Toast.makeText(mContext, comments?.message, Toast.LENGTH_SHORT).show()
                (mContext as CommentsActivity).requestCommentList()
            }

            override fun onFailure(call: Call<CommentsList>, t: Throwable) {
            }

        })
    }

    override fun getItemCount(): Int {
        return commentsList!!.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var comments_profile : ImageView = itemView.findViewById(R.id.comments_profile)
        var comments_nickname : TextView = itemView.findViewById(R.id.comments_nickname)
        var comment_datetime : TextView = itemView.findViewById(R.id.comment_datetime)
        var comment_content : TextView = itemView.findViewById(R.id.comment_content)
        var setComments : ImageButton = itemView.findViewById(R.id.setComments)



        fun bind(item: CommentsList) {

            comments_nickname.text = item.comment_nickname
            comment_datetime.text = item.comment_datetime
            comment_content.text = item.comment_content
            var uriToString : String = item.comment_profile
            if(uriToString != null){

                var uri : Uri = Uri.parse(uriToString)
                var uri_diary = "http://3.36.52.195/profile/"+uri
                Glide.with(itemView).load(uri_diary).into(comments_profile)
            }else{
                comments_profile.setImageResource(R.drawable.basic_profile)
            }

        }
    }

}
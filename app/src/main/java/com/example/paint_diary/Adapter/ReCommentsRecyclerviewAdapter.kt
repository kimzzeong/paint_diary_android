package com.example.paint_diary.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.CommentsList
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class ReCommentsRecyclerviewAdapter: RecyclerView.Adapter<ReCommentsRecyclerviewAdapter.ViewHolder>() {

    var reCommentsList: ArrayList<CommentsList>? = null
    var mContext : Context? = null
    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var comments_profile : ImageView = itemView.findViewById(R.id.comments_profile)
        var comments_nickname : TextView = itemView.findViewById(R.id.comments_nickname)
        var comment_datetime : TextView = itemView.findViewById(R.id.comment_datetime)
        var comment_content : TextView = itemView.findViewById(R.id.comment_content)
        var setComments : ImageButton = itemView.findViewById(R.id.setComments)



        fun bind(item: CommentsList) {
            //requestCommentList()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.reomments_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentItem: CommentsList = reCommentsList!!.get(position)
        holder.bind(reCommentsList!!.get(position))

    }

    override fun getItemCount(): Int {
       return reCommentsList!!.size
    }

//    fun requestCommentList(comment_idx: Int) {
//        var comment = retrofit.create(IRetrofit::class.java)
//        comment.requestReComments(comment_idx).enqueue(object : Callback<ArrayList<CommentsList>> {
//            override fun onResponse(call: Call<ArrayList<CommentsList>>, response: Response<ArrayList<CommentsList>>) {
//                var recomments = response.body()!!
//                Log.e("size",""+recomments.size)
//                reCommentsList = recomments
//
//                notifyDataSetChanged()
//            }
//
//            override fun onFailure(call: Call<ArrayList<CommentsList>>, t: Throwable) {
//            }
//
//        })
//    }


}
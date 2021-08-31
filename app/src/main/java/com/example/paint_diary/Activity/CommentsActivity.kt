package com.example.paint_diary.Activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_diary.Adapter.CommentsRecyclerviewAdapter
import com.example.paint_diary.Data.CommentsList
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CommentsActivity : AppCompatActivity() {

    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    var secret : Int = 0 //댓글 공개 여부
    var diary_idx : Int = 0
    lateinit var commentsRecyclerview: CommentsRecyclerviewAdapter
    var commentsList : ArrayList<CommentsList>? = null

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        comments_toolbar.setTitleTextColor(Color.BLACK)
        comments_toolbar.setTitle("댓글")
       val intent = intent
       diary_idx = intent.getIntExtra("diary_idx", 0)

       commentsRecyclerview = CommentsRecyclerviewAdapter(this)
       requestCommentList()
       val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
       comments_list.layoutManager = layoutManager

        //댓글 비밀글
        comments_secret.setOnClickListener {
            if(secret == 0){
                comments_secret.setImageResource(R.drawable.ic_baseline_lock_24)
                secret = 1
            }else{
                comments_secret.setImageResource(R.drawable.ic_baseline_lock_open_24)
                secret = 0
            }
        }

       //댓글 등록
        comments_send.setOnClickListener {
            CommentSend()
        }

       //댓글 목록 새로고침
       comments_refresh.setOnRefreshListener{
           requestCommentList()
           comments_refresh.isRefreshing = false
       }

    }

    private fun requestCommentCount() {
        var comment = retrofit.create(IRetrofit::class.java)
        comment.requestCommentsCount(diary_idx).enqueue(object : Callback<CommentsList> {
            override fun onResponse(call: Call<CommentsList>, response: Response<CommentsList>) {
                var count = response.body()
                comments_count.text = count?.comment_count.toString()

            }

            override fun onFailure(call: Call<CommentsList>, t: Throwable) {
                Log.e("error",t.localizedMessage)
            }

        })
    }

    fun requestCommentList() {
        var comment = retrofit.create(IRetrofit::class.java)
        comment.requestComments(diary_idx).enqueue(object : Callback<ArrayList<CommentsList>> {
            override fun onResponse(call: Call<ArrayList<CommentsList>>, response: Response<ArrayList<CommentsList>>) {
                commentsList = response.body()!!
                comments_list.adapter = commentsRecyclerview
                commentsRecyclerview.commentsList = commentsList
                commentsRecyclerview.notifyDataSetChanged()
                requestCommentCount()
            }

            override fun onFailure(call: Call<ArrayList<CommentsList>>, t: Throwable) {
            }

        })
    }

    private fun hideKeyBoard() {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

    }

    private fun CommentSend() {
        var commentSend = retrofit.create(IRetrofit::class.java)
        var intent = intent
        var diary_idx = intent.getIntExtra("diary_idx", 0)
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", ""))

        commentSend.sendComments(diary_idx, comments_edit.text.toString(), user_idx, secret).enqueue(object : Callback<CommentsList> {
            override fun onResponse(call: Call<CommentsList>, response: Response<CommentsList>) {
                val comment = response.body()
                Toast.makeText(this@CommentsActivity, comment?.message, Toast.LENGTH_SHORT).show()
                comments_edit.setText(null)
                requestCommentList()
                hideKeyBoard()
            }

            override fun onFailure(call: Call<CommentsList>, t: Throwable) {
                Toast.makeText(this@CommentsActivity, "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                Log.e("error", t.localizedMessage)
            }

        })
    }

}
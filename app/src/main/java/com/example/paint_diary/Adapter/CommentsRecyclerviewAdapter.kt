package com.example.paint_diary.Adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Activity.CommentsActivity
import com.example.paint_diary.Data.CommentsList
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.comments_dialog.view.*
import kotlinx.android.synthetic.main.comments_modify_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CommentsRecyclerviewAdapter(commentsActivity: CommentsActivity) :RecyclerView.Adapter<CommentsRecyclerviewAdapter.ViewHolder>(){

    var commentsList: ArrayList<CommentsList>? = null
    var commentsActivity = commentsActivity

    lateinit var recommentsRecyclerview: ReCommentsRecyclerviewAdapter
    var mContext : Context? = null
    var activity : CommentsActivity? = null
    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    var user_idx : Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val sharedPreferences = mContext?.getSharedPreferences("user", Context.MODE_PRIVATE)
        user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", "0"))
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comments_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentItem: CommentsList = commentsList!!.get(position)
        holder.bind(commentsList!!.get(position))
        val sharedPreferences = mContext?.getSharedPreferences("user", Context.MODE_PRIVATE)
       // var user_idx = Integer.parseInt(sharedPreferences?.getString("user_idx", "0"))
        Log.e("commentsAdapter",user_idx.toString())
        if(commentItem.comment_writer == user_idx){
            holder.comments_layout.setBackgroundColor(Color.parseColor("#D4E5ED")) //#24EFCAA6
            holder.setComments.setOnClickListener{

                val comments_dialog = LayoutInflater.from(mContext).inflate(R.layout.comments_dialog,null)
                val mBuilder = AlertDialog.Builder(mContext!!)
                    .setView(comments_dialog)

                val mComments_dialog_AlertDialog = mBuilder.show()
                var secret = commentItem.comment_secret
                if(secret == 0){
                    comments_dialog.comments_dialog_secret.text = "??????????????? ??????"
                }else{
                    comments_dialog.comments_dialog_secret.text = "??????????????? ??????"
                }

                //??????????????? ?????? ?????? ?????? ??????
                comments_dialog.comments_dialog_secret.setOnClickListener {
                    if(secret == 0){
                        secret = 1
                    }else{
                        secret = 0
                    }
                    requestCommentSecret(commentItem.comment_idx,secret)
                    mComments_dialog_AlertDialog.dismiss()
                }

                //??????????????? ?????? ?????? ??????
                comments_dialog.comments_dialog_modify.setOnClickListener {
                    val mDialog = LayoutInflater.from(mContext).inflate(R.layout.comments_modify_dialog, null)
                    val mBuilder = AlertDialog.Builder(mContext!!)
                        .setView(mDialog)

                    val mAlertDialog = mBuilder.show()
                    mDialog.comment_set_edit.setText(commentItem.comment_content)

                    //?????? ?????? ??????????????? ???????????? ?????????
                    mDialog.comment_set_ok.setOnClickListener {
                        requestCommentsModify(commentItem.comment_idx, mDialog.comment_set_edit.text.toString())
                        mAlertDialog.dismiss()
                    }
                    //?????? ?????? ??????????????? ???????????? ?????????
                    mDialog.comment_set_cancel.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    mDialog.comment_set_edit.clearFocus(); //hidden keyboard

                    mComments_dialog_AlertDialog.dismiss()
                }

                //?????????
                comments_dialog.comments_dialog_recomments.setOnClickListener {
                    commentsActivity.comments_edit.hint = "@" + holder.comments_nickname.text.toString() + "????????? ??????..."
                            commentsActivity.commentsSendStatus = 1
                            commentsActivity.comment_idx = commentItem.comment_idx
                }

                //??????????????? ?????? ??????
                comments_dialog.comments_dialog_remove.setOnClickListener {
                    val dialog = AlertDialog.Builder(mContext!!)
                            dialog.setMessage("?????? ?????????????????????????")
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("???") { dialog, id ->

                                requestCommentsRemove(commentItem.comment_idx)

                            }
                            dialog.setNegativeButton("?????????") { dialog, id ->

                            }
                            dialog.show()

                    mComments_dialog_AlertDialog.dismiss()
                }

                //??????????????? ?????? ??????
                comments_dialog.comments_dialog_cancel.setOnClickListener {
                    mComments_dialog_AlertDialog.dismiss()
                }
            }

            //more ????????? ?????? ??? ????????????
//            val commentsPopup = PopupMenu(mContext!!, holder.setComments)
//            commentsPopup.menuInflater?.inflate(R.menu.comments_modify_menu, commentsPopup.menu)
//
//            holder.setComments.setOnClickListener {
//
//                commentsPopup.setOnMenuItemClickListener { menuItem ->
//                    when (menuItem.itemId) {
//                        R.id.comments_modify -> {
//                            val mDialog = LayoutInflater.from(mContext).inflate(R.layout.comments_modify_dialog, null)
//                            val mBuilder = AlertDialog.Builder(mContext!!)
//                                .setView(mDialog)
//
//                            val mAlertDialog = mBuilder.show()
//                            mDialog.comment_set_edit.setText(commentItem.comment_content)
//
//                            //?????? ?????? ??????????????? ???????????? ?????????
//                            mDialog.comment_set_ok.setOnClickListener {
//                                requestCommentsModify(commentItem.comment_idx, mDialog.comment_set_edit.text.toString())
//                                mAlertDialog.dismiss()
//                            }
//                            //?????? ?????? ??????????????? ???????????? ?????????
//                            mDialog.comment_set_cancel.setOnClickListener {
//                                mAlertDialog.dismiss()
//                            }
//
//                            mDialog.comment_set_edit.clearFocus(); //hidden keyboard
//                        }
//
//                        R.id.comments_remove -> {
//                            val dialog = AlertDialog.Builder(mContext!!)
//                            dialog.setMessage("?????? ?????????????????????????")
//                            dialog.setCancelable(false);
//                            dialog.setPositiveButton("???") { dialog, id ->
//
//                                requestCommentsRemove(commentItem.comment_idx)
//
//                            }
//                            dialog.setNegativeButton("?????????") { dialog, id ->
//
//                            }
//                            dialog.show()
//                        }
//
//                        R.id.comments_recomment -> {
//
//                            commentsActivity.comments_edit.hint = "@" + holder.comments_nickname.text.toString() + "????????? ??????..."
//                            commentsActivity.commentsSendStatus = 1
//                            commentsActivity.comment_idx = commentItem.comment_idx
//
//                        }
//
//                    R.id.comments_secret -> {
//                        val mDialog = LayoutInflater.from(mContext).inflate(R.layout.comments_dialog,null)
//                        val mBuilder = AlertDialog.Builder(mContext!!)
//                                .setView(mDialog)
//
//                        val mAlertDialog = mBuilder.show()
//
//                        mDialog.comments_dialog_cancel.setOnClickListener {
//                            mAlertDialog.dismiss()
//                        }
////                        var secret = commentItem.comment_secret
////                        if(secret == 1 ){
////                            menuItem.title = "????????? ??????"
////                            set_comments_secret(commentItem.comment_idx,)
////                        }
//
//                    }
//
//                    }
//                    false
//                }
//
//                commentsPopup.show()
//            }
        }else{

            holder.setComments.setOnClickListener {
                holder.comments_layout.setBackgroundColor(Color.WHITE)

                val comments_dialog = LayoutInflater.from(mContext).inflate(R.layout.comments_dialog,null)
                val mBuilder = AlertDialog.Builder(mContext!!)
                    .setView(comments_dialog)
                val mComments_dialog_AlertDialog = mBuilder.show()

                comments_dialog.comments_dialog_secret.visibility = View.GONE
                comments_dialog.comments_dialog_modify.visibility = View.GONE
                comments_dialog.comments_dialog_remove.visibility = View.GONE
                comments_dialog.comments_dialog_recomments.setOnClickListener {
                    commentsActivity.comments_edit.hint = "@" + holder.comments_nickname.text.toString() + "????????? ??????..."
                    commentsActivity.commentsSendStatus = 1
                    commentsActivity.comment_idx = commentItem.comment_idx
                    mComments_dialog_AlertDialog.dismiss()
                }

                comments_dialog.comments_dialog_cancel.setOnClickListener {
                    mComments_dialog_AlertDialog.dismiss()
                }
            }

            //more ????????? ?????? ??? ????????????
//            val commentsPopup = PopupMenu(mContext!!, holder.setComments)
//
//            commentsPopup.menuInflater?.inflate(R.menu.recomments_menu, commentsPopup.menu)

//            holder.setComments.setOnClickListener {
//                commentsPopup.setOnMenuItemClickListener { menuItem ->
//                    when (menuItem.itemId) {
//                        R.id.comments_recomment -> {
//                            commentsActivity.comments_edit.hint = "@" + holder.comments_nickname.text.toString() + "????????? ??????..."
//                            commentsActivity.commentsSendStatus = 1
//                            commentsActivity.comment_idx = commentItem.comment_idx
//
//                        }
//
////                    R.id.comments_secret -> {
////                        val mDialog = LayoutInflater.from(mContext).inflate(R.layout.comments_dialog,null)
////                        val mBuilder = AlertDialog.Builder(mContext!!)
////                                .setView(mDialog)
////
////                        val mAlertDialog = mBuilder.show()
////
////                        mDialog.comments_dialog_cancel.setOnClickListener {
////                            mAlertDialog.dismiss()
////                        }
//////                        var secret = commentItem.comment_secret
//////                        if(secret == 1 ){
//////                            menuItem.title = "????????? ??????"
//////                            set_comments_secret(commentItem.comment_idx,)
//////                        }
////
////                    }
//
//                    }
//                    false
//                }
//
//                commentsPopup.show()
//            }
        }


        //????????? ????????? ????????????????????? ??????
        //requestCommentList(commentItem.comment_idx)
        var comment = retrofit.create(IRetrofit::class.java)
        comment.requestReComments(commentItem.comment_idx).enqueue(object : Callback<ArrayList<CommentsList>> {
            override fun onResponse(call: Call<ArrayList<CommentsList>>, response: Response<ArrayList<CommentsList>>) {
                var recomments = response.body()!!
                Log.e("onResponse",""+recomments!!.size)
                recommentsRecyclerview = ReCommentsRecyclerviewAdapter(commentsActivity.diary_writer)
                holder.recomments_list.adapter = recommentsRecyclerview
                recommentsRecyclerview.reCommentsList = recomments
                recommentsRecyclerview.notifyDataSetChanged()
                val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                holder.recomments_list.layoutManager = layoutManager
            }

            override fun onFailure(call: Call<ArrayList<CommentsList>>, t: Throwable) {
                Log.e("onFailure",""+t.localizedMessage)
            }

        })

    }


    //?????? ??????
    private fun requestCommentsModify(comment_idx: Int, comment_content: String) {
        var requestCommentsModify = retrofit.create(IRetrofit::class.java)
        requestCommentsModify.requestModifyComments(comment_idx!!, comment_content).enqueue(object : Callback<CommentsList> {
            override fun onResponse(call: Call<CommentsList>, response: Response<CommentsList>) {
                val comments = response.body()
                Toast.makeText(mContext, comments?.message, Toast.LENGTH_SHORT).show()
                (mContext as CommentsActivity).requestCommentList()
            }

            override fun onFailure(call: Call<CommentsList>, t: Throwable) {
            }

        })
    }

    //?????? ??????
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

    //???????????? ??????
    private fun requestCommentSecret(comment_idx: Int,comment_secret: Int) {
        var requestSecret = retrofit.create(IRetrofit::class.java)
        requestSecret.requestCommentsSecret(comment_idx!!,comment_secret).enqueue(object : Callback<CommentsList> {
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
        var recomments_list : RecyclerView = itemView.findViewById(R.id.recomments_list)
        var comments_layout : ConstraintLayout = itemView.findViewById(R.id.comments_layout)
        var comments_lock : ImageView = itemView.findViewById(R.id.comments_lock)


        fun bind(item: CommentsList) {

            //??????????????? ?????? ??????
            if(item.comment_secret == 0){
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
            //??????????????? ??????
            else{
                comments_lock.visibility = View.VISIBLE
                //?????? ????????? ?????? ????????? ??????????????? ??????????????? ??????
                if(item.comment_writer == user_idx || commentsActivity.diary_writer == user_idx){
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

                }else{

                    setComments.visibility = View.INVISIBLE
                    comments_nickname.text = "????????????"
                    comment_datetime.text = item.comment_datetime
                    comment_content.text = "???????????? ?????????."


                }
            }


        }
    }

}
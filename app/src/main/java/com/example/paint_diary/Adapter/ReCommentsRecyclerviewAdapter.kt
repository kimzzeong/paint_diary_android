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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.Activity.CommentsActivity
import com.example.paint_diary.Data.CommentsList
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.comments_modify_dialog.view.*
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

        //more 이미지 클릭 시 팝업메뉴
        val commentsPopup = PopupMenu(mContext!!, holder.setComments)
        commentsPopup.menuInflater?.inflate(R.menu.recomments_modify_menu, commentsPopup.menu)

        holder.setComments.setOnClickListener {

            commentsPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.comments_modify -> {
                        val mDialog = LayoutInflater.from(mContext).inflate(R.layout.comments_modify_dialog, null)
                        val mBuilder = AlertDialog.Builder(mContext!!)
                            .setView(mDialog)

                        val mAlertDialog = mBuilder.show()
                        mDialog.comment_set_edit.setText(commentItem.comment_content)

                        //댓글 수정 다이얼로그 확인버튼 클릭시
                        mDialog.comment_set_ok.setOnClickListener {
                            requestReCommentsModify(commentItem.recomment_idx, mDialog.comment_set_edit.text.toString())
                            mAlertDialog.dismiss()
                        }
                        //댓글 수정 다이얼로그 취소버튼 클릭시
                        mDialog.comment_set_cancel.setOnClickListener {
                            mAlertDialog.dismiss()
                        }

                        mDialog.comment_set_edit.clearFocus(); //hidden keyboard
                    }

                    R.id.comments_remove -> {
                        val dialog = AlertDialog.Builder(mContext!!)
                        dialog.setMessage("정말 삭제하시겠습니까?")
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("네") { dialog, id ->

                            RerequestCommentsRemove(commentItem.recomment_idx)

                        }
                        dialog.setNegativeButton("아니오") { dialog, id ->

                        }
                        dialog.show()
                    }


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
////                            menuItem.title = "공개로 전환"
////                            set_comments_secret(commentItem.comment_idx,)
////                        }
//
//                    }

                }
                false
            }

            commentsPopup.show()
        }

    }

    private fun requestReCommentsModify(recommentIdx: Int, comment_content: String) {
        var requestCommentsModify = retrofit.create(IRetrofit::class.java)
        requestCommentsModify.requestModifyReComments(recommentIdx!!, comment_content).enqueue(object : Callback<CommentsList> {
            override fun onResponse(call: Call<CommentsList>, response: Response<CommentsList>) {
                val comments = response.body()
                Toast.makeText(mContext, comments?.message, Toast.LENGTH_SHORT).show()
                (mContext as CommentsActivity).requestCommentList()
            }

            override fun onFailure(call: Call<CommentsList>, t: Throwable) {
            }

        })
    }

    private fun RerequestCommentsRemove(recommentIdx: Int) {
        var requestDiaryRemove = retrofit.create(IRetrofit::class.java)
        //대댓글 삭제로 가야함 지금 댓글 삭제임
        requestDiaryRemove.requestRemoveReComments(recommentIdx!!).enqueue(object : Callback<CommentsList> {
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
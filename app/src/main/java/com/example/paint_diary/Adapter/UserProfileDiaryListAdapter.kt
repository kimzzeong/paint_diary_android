package com.example.paint_diary.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.DiaryList
import com.example.paint_diary.DiaryRequest
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_diary_info.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserProfileDiaryListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var diary_List: ArrayList<DiaryList>? = null
    lateinit var context : Context

    interface ItemClickListener {
        fun onClick(view: View, position: Int, diary_idx :Int, diary_wirter : Int)
    }


    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when(viewType){
            DiaryList.DATE_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.profile_diary_date,
                    parent,
                    false
                )
                DateViewHolder(view)
            }
            DiaryList.DIARY_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.profile_diary_item,
                    parent,
                    false
                )
                DiaryViewHolder(view)
            }
           else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
         }
    }

    fun UserProfileDiaryListAdapter(context: Context) {
        this.context = context
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val obj = diary_List?.get(position)
        when (obj?.type) {
            DiaryList.DATE_TYPE -> {
                (holder as DateViewHolder).user_profile_date_group.text = obj.diary_date.substring(0,7)
            }
            DiaryList.DIARY_TYPE -> {

                (holder as DiaryViewHolder).bind(diary_List!!.get(position))
//                Glide.with(holder.itemView.context)
//                    .load("http://3.36.52.195/diary/"+obj.diary_painting)
//                    .circleCrop()
//                    .into((holder as DiaryViewHolder).profile_diary_painting)
//
//                holder.profile_diary_title.text = obj.diary_title
               // (holder as DiaryViewHolder).profile_diary_date_day.text = obj.diary_date
                    //getDateDay(obj.diary_date,"Y-m-d H:i:s")
               // holder.profile_diary_date_day_num
                holder.itemView.setOnClickListener {
                    itemClickListner.onClick(it, position,diary_List!!.get(position).diary_idx,diary_List!!.get(position).diary_writer)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return diary_List?.get(position)!!.type
    }

        inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val user_profile_date_group: TextView = itemView.findViewById(R.id.user_profile_date_group)

        }
        inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val profile_diary_painting: ImageView = itemView.findViewById(R.id.profile_diary_painting)
            val profile_diary_title: TextView = itemView.findViewById(R.id.profile_diary_title)
            val profile_diary_date_day_num: TextView = itemView.findViewById(R.id.profile_diary_date_day_num)
            val profile_diary_date_day: TextView = itemView.findViewById(R.id.profile_diary_date_day)

            fun bind(item: DiaryList) {
                profile_diary_title.text = item.diary_title
                profile_diary_date_day.text = getDateDay(item.diary_date,"yyyy-MM-dd HH:mm:ss")
                var date = item.diary_date.substring(8,10)
                profile_diary_date_day_num.text =date
                var uriToString : String = item.diary_painting
                var uri : Uri = Uri.parse(uriToString)
                var uri_diary = "http://3.36.52.195/diary/"+uri
                 Glide.with(itemView).load(uri_diary).into(profile_diary_painting)

            }

        }

    override fun getItemCount(): Int {
        return diary_List!!.size
    }





    @Throws(Exception::class)
    fun getDateDay(date: String?, dateType: String?): String? {
        var day = ""
        val dateFormat = SimpleDateFormat(dateType)
        val nDate: Date = dateFormat.parse(date)
        val cal: Calendar = Calendar.getInstance()
        cal.setTime(nDate)
        val dayNum: Int = cal.get(Calendar.DAY_OF_WEEK)
        when (dayNum) {

            1 -> day = "일요일"
            2 -> day = "월요일"
            3 -> day = "화요일"
            4 -> day = "수요일"
            5 -> day = "목요일"
            6 -> day = "금요일"
            7 -> day = "토요일"
        }
        return day
    }

}
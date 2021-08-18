package com.example.paint_diary.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.DiaryList
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_diary_info.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserProfileDiaryListAdapter(private var diary_list: ArrayList<DiaryList>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context : Context
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

    fun UserProfileDiaryListAdapter(list: ArrayList<DiaryList>, context: Context) {
        diary_list = list
        this.context = context
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj = diary_list[position]
        when (obj.type) {
            DiaryList.DATE_TYPE -> {
                (holder as DateViewHolder).user_profile_date_group.text = obj.diary_date
            }
            DiaryList.DIARY_TYPE -> {
                Glide.with(context)
                    .load(obj.diary_painting)
                    .circleCrop()
                    .into((holder as DiaryViewHolder).profile_diary_painting)

                holder.profile_diary_title.text = obj.diary_title
                holder.profile_diary_date_day.text = getDateDay(obj.diary_date,"Y-m-d H:i:s")
                holder.profile_diary_date_day_num
            }
        }

    }

    override fun getItemCount(): Int {
        return diary_list.size
    }


    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user_profile_date_group: TextView = itemView.findViewById(R.id.user_profile_date_group)

    }
    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val profile_diary_painting: ImageView = itemView.findViewById(R.id.profile_diary_painting)
        val profile_diary_title: TextView = itemView.findViewById(R.id.profile_diary_title)
        val profile_diary_date_day_num: TextView = itemView.findViewById(R.id.profile_diary_date_day_num)
        val profile_diary_date_day: TextView = itemView.findViewById(R.id.profile_diary_date_day)

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
            1 -> day = "일"
            2 -> day = "월"
            3 -> day = "화"
            4 -> day = "수"
            5 -> day = "목"
            6 -> day = "금"
            7 -> day = "토"
        }
        return day
    }

}
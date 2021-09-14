package com.example.paint_diary.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_diary.R
import java.util.ArrayList

class CalendarAdapter: RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    var daysOfMonth: ArrayList<String>? = null

    interface ItemClickListener {
        fun onClick(dayText : String)
    }
    private var itemClickListner : ItemClickListener? = null

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var dayOfMonth : TextView = itemView.findViewById(R.id.cellDayText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)
        val layoutParmas : ViewGroup.LayoutParams = v.layoutParams
        layoutParmas.height = ((parent.height * 0.16666666).toInt())
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayOfMonth.text = daysOfMonth?.get(position)
        holder.itemView.setOnClickListener {

            Log.e("dayOfMonth",holder.dayOfMonth.text.toString())
          //  Log.e("position",position.toString())
            itemClickListner?.onClick(holder.dayOfMonth.text.toString())
        }
    }

    override fun getItemCount(): Int {
        return daysOfMonth!!.size
    }
}
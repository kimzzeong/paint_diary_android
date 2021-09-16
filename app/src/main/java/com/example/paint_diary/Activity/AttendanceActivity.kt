package com.example.paint_diary.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.paint_diary.Adapter.CalendarAdapter
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_attendance.*
import kotlinx.android.synthetic.main.calendar_cell.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class AttendanceActivity : AppCompatActivity() {


    lateinit var calendarAdapter: CalendarAdapter
    lateinit var selectedDate : LocalDate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        selectedDate = LocalDate.now()
        setMonthView()

        calendar_pre.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
        }

        calendar_next.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }

        btn_attendance.setOnClickListener {
            Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()
        }

        calendarAdapter.setItemClickListener(object : CalendarAdapter.ItemClickListener {
            override fun onClick(dayText: String) {
                Log.e("dayText",dayText)
                if (!dayText.equals("")) {
                    var message: String = " " + monthYearFromDate(selectedDate) + " " + dayText
                    Log.e("monthYearFromDate",monthYearFromDate(selectedDate))
                    Toast.makeText(this@AttendanceActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun setMonthView() {
        monthYear.text = monthYearFromDate(selectedDate)
        var daysInMonth : ArrayList<String> = daysInMonthArray(selectedDate)
        calendarAdapter = CalendarAdapter()
        val layoutManager = GridLayoutManager(this, 7)
        calendarRecyclerview.layoutManager = layoutManager
        calendarAdapter.daysOfMonth = daysInMonth
        calendarRecyclerview.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val dayInMonthArray : ArrayList<String> = ArrayList()
        var yearMonth : YearMonth = YearMonth.from(date)
        var daysInMonth = yearMonth.lengthOfMonth()

        var firstOfMonth : LocalDate = selectedDate.withDayOfMonth(1)
        var dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42){
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek){
                dayInMonthArray.add("")
            }else{
                dayInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return dayInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy MMMM")
        return date.format(formatter)
    }


}
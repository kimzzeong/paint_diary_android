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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
    }

}
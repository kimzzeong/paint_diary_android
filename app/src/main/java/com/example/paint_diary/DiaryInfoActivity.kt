package com.example.paint_diary

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DiaryInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_info)

        val intent = intent
        var diary_idx : Int ? = null
        diary_idx = intent.getIntExtra("diary_idx",0)
        Log.e("intent", intent.getIntExtra("diary_idx",0).toString())


    }
}
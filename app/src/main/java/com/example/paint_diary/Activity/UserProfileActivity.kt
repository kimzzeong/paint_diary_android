package com.example.paint_diary.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.R

class UserProfileActivity : AppCompatActivity() {
    private var diary_wirter: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val intent = intent
        diary_wirter = intent.getIntExtra("diary_wirter",0)


    }


}
package com.example.paint_diary.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.R

class AppPassWordActivity : AppCompatActivity() {
    private var oldPwd = ""
    private var changePwdUnlock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_pass_word)
    }
}
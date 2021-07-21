package com.example.paint_diary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_join_memebership.*
import kotlinx.android.synthetic.main.activity_profile_modify.*
import kotlinx.android.synthetic.main.fragment_home.*

class ProfileModifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)

        profile_toolbar.inflateMenu(R.menu.profile_save_menu)
        profile_toolbar.setTitleTextColor(Color.BLACK)
        profile_toolbar.setTitle("프로필 수정")
    }
}
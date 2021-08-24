package com.example.paint_diary.Activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_setting.*

class CommentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        comments_toolbar.setTitleTextColor(Color.BLACK)
        comments_toolbar.setTitle("댓글")
    }
}
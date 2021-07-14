package com.example.paint_diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var write_btn : FloatingActionButton = findViewById(R.id.write_btn);

//        val intent = Intent(this, WriteActivity::class.java) // 인텐트를 생성
//
//        write_btn.setOnClickListener {
//            startActivity(intent)
//        }
        val intent = Intent(this, LoginActivity::class.java) // 인텐트를 생성
        write_btn.setOnClickListener{
            startActivity(intent)
        }
    }
}
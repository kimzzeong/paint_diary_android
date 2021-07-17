package com.example.paint_diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.activity_join_memebership.*

class JoinMemebershipActivity : AppCompatActivity() {

    private lateinit var progressDialog: AppCompatDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_memebership)

        //progressDialog.setMessage("잠시만 기다려주세요...")

        join_btn.setOnClickListener{
            //유저정보 저장
            saveUserInfo()
            Toast.makeText(this,"클릭",Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfo() {
        //progressDialog.show()
        //프로그래스 바 추가하기


    }
}
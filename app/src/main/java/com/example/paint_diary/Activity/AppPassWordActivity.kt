package com.example.paint_diary.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_app_pass_word.*

class AppPassWordActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_pass_word)

        btn_00.setOnClickListener(this)
        btn_01.setOnClickListener(this)
        btn_02.setOnClickListener(this)
        btn_03.setOnClickListener(this)
        btn_04.setOnClickListener(this)
        btn_05.setOnClickListener(this)
        btn_06.setOnClickListener(this)
        btn_07.setOnClickListener(this)
        btn_08.setOnClickListener(this)
        btn_09.setOnClickListener(this)
        btn_clear.setOnClickListener(this)
        btn_pre.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
          R.id.btn_00 -> { Toast.makeText(this,"0",Toast.LENGTH_SHORT).show()}
          else -> {}
        }
    }
}
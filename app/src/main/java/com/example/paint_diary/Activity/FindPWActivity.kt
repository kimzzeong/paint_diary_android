package com.example.paint_diary.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_find_p_w.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class FindPWActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_p_w)
        dialog_layout.visibility = View.INVISIBLE

        btn_findPW.setOnClickListener {
            //Toast.makeText(this,findPW_email.text.toString(),Toast.LENGTH_SHORT).show()

            dialog_layout.visibility = View.VISIBLE
            dialog_layout.bringToFront()

            var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            var findPW = retrofit.create(IRetrofit::class.java)

            findPW.requestFindPW(findPW_email.text.toString()).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    dialog_layout.visibility = View.INVISIBLE
                    val findpw = response.body()
                    Toast.makeText(this@FindPWActivity,findpw.toString(),Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@FindPWActivity,"다시 시도해 주세요.",Toast.LENGTH_SHORT).show()
                }

            })

        }
    }
}
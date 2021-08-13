package com.example.paint_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_change_p_w.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChangePWActivity : AppCompatActivity() {
    var user_idx : String ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_p_w)

        val intent = intent
        user_idx = intent.getStringExtra("user_idx")
        Log.e("intent", user_idx!!)


        btn_changePW.setOnClickListener {
            updatePW()
        }
    }

    private fun updatePW() {
        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var changePW = retrofit.create(IRetrofit::class.java)

        if (user_idx != null) {
            changePW.updatePW(user_idx!!,nowPW.text.toString(),change_password.text.toString(),change_password_check.text.toString()).enqueue(object: Callback<ChanePW> {
                override fun onResponse(call: Call<ChanePW>, response: Response<ChanePW>) {
                    var password = response.body()
                    if(password?.status == 1){
                        Toast.makeText(this@ChangePWActivity,password?.message,Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ChangePWActivity,MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@ChangePWActivity,password?.message,Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ChanePW>, t: Throwable) {
                }

            })
        }
    }
}
package com.example.paint_diary

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_join_memebership.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JoinMemebershipActivity : AppCompatActivity() {

    private lateinit var progressDialog: AppCompatDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_memebership)

        var join_email : EditText = findViewById(R.id.join_email)
        var join_nickname : EditText = findViewById(R.id.join_nickname)
        var join_password : EditText = findViewById(R.id.join_password)
        var join_password_check : EditText = findViewById(R.id.join_password_check)
        var join_btn : Button = findViewById(R.id.join_btn)

        //progressDialog.setMessage("잠시만 기다려주세요...")

        var gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        join_btn.setOnClickListener {
            if(join_password.text.toString().equals(join_password_check.text.toString())){ //비밀번호와 비밀번호 확인 값이 일치하면
                var joinMembership = retrofit.create(IRetrofit::class.java)
                joinMembership.requestJoinMembership(join_email.text.toString(),join_nickname.text.toString(),join_password.text.toString()).enqueue(object: Callback<JoinMembership> {
                    //웹 통신 성공, 응답값을 받아옴
                    override fun onResponse(call: Call<JoinMembership>, response: Response<JoinMembership>) {
                        //var test = response.body()
                        finish()
                        Toast.makeText(this@JoinMemebershipActivity,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                    }

                    //웹 통신 실패
                    override fun onFailure(call: Call<JoinMembership>, t: Throwable) {
                        Toast.makeText(this@JoinMemebershipActivity,"실패",Toast.LENGTH_SHORT).show()
                        Log.e("에러",t.localizedMessage)
                    }
                })
            }else{
                Toast.makeText(this@JoinMemebershipActivity,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
            }

        }
    }
}
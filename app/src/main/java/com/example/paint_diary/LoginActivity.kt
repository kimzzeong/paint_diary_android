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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //쉐어드 프리퍼런스(자동로그인 위함)
        val sharedPreferences = this.getSharedPreferences("user",0)
        val editor = sharedPreferences.edit()

        Log.e("TAG", "쉐어드에 저장된 아이디 = " + sharedPreferences.getString("user_idx", ""))
        var login_email : EditText = findViewById(R.id.login_email)
        var login_password : EditText = findViewById(R.id.login_password)
        var login_btn : Button = findViewById(R.id.login_btn)
        var login_find_password : TextView = findViewById(R.id.login_find_password)
        var login_to_join : TextView = findViewById(R.id.login_to_join)

        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var loginService = retrofit.create(IRetrofit::class.java)


        login_btn.setOnClickListener{


            loginService.requestLogin(login_email.text.toString(),login_password.text.toString()).enqueue(object: Callback<Login>{
                //웹 통신 성공, 응답값을 받아옴
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    var login = response.body()
                    if(login?.user_idx != null){
                        editor.putString("user_idx", login?.user_idx)
                        editor.putString("user_nickname", login?.user_nickname)
                        editor.apply()
                        Log.e("TAG", "쉐어드에 저장된 아이디 = " + sharedPreferences.getString("user_idx", ""))
                        Log.e("TAG", "쉐어드에 저장된 닉네임 = " + sharedPreferences.getString("user_nickname", ""))
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@LoginActivity,login?.user_nickname+"님 반갑습니다.",Toast.LENGTH_SHORT).show()
                    }else{
                        //null값 올 때 처리, 여기서는 예외처리 부분
                        Toast.makeText(this@LoginActivity,"null",Toast.LENGTH_SHORT).show()
                    }
                }

                //웹 통신 실패
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Toast.makeText(this@LoginActivity,"실패",Toast.LENGTH_SHORT).show()
                    Log.e("에러",t.localizedMessage)
                }

            })
//            val intent = Intent(this, MainActivity::class.java) // 인텐트를 생성
//            startActivity(intent)

        }
        login_find_password.setOnClickListener{
            Toast.makeText(this@LoginActivity,"비번찾기",Toast.LENGTH_SHORT).show()
        }
        login_to_join.setOnClickListener{
            //Toast.makeText(this@LoginActivity,"회원가입",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, JoinMemebershipActivity::class.java) // 인텐트를 생성
            startActivity(intent)
        }

    }
}
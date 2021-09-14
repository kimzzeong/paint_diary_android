package com.example.paint_diary.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.example.paint_diary.Data.Withdrawal
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setting_toolbar.setTitleTextColor(Color.BLACK)
        setting_toolbar.setTitle("설정")

        val sharedPreferences = this.getSharedPreferences("user",0)
        val editor = sharedPreferences.edit()
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")


        var passMode = sharedPreferences?.getString("passMode","")
        if(passMode.equals(null) || passMode.equals("")){
            setting_lock.text = "앱 잠금 설정"
        }else{

            setting_lock.text = "앱 잠금 설정 해제"
        }

        //앱 잠금 설정
        setting_lock.setOnClickListener {
            if(passMode.equals(null) || passMode.equals("")){
                val intent = Intent(this, AppPassWordActivity::class.java)
                intent.putExtra("settingActivity","setting")
                startActivity(intent)
            }else{
                    //쉐어드에 비밀번호와 앱 잠금 상태인지 아닌지 체크
                    val sharedPreferences = this.getSharedPreferences("user", 0)
                    val editor = sharedPreferences.edit()
                    editor.remove("passMode")
                 //   editor.remove("passcode")
                    editor.commit()
                    setting_lock.text = "앱 잠금 설정"
                Toast.makeText(this,"앱 잠금이 해제되었습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        //로그아웃 클릭
        setting_logout.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("정말로 로그아웃하시겠습니까?")
            dialog.setCancelable(false);
            dialog.setPositiveButton("네"){ dialog, id ->
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
                editor.remove("user_idx")
                editor.commit()
                Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show()
            }
            dialog.setNegativeButton("아니오"){ dialog, id ->
            }
            dialog.show()
        }

        setting_change_PW.setOnClickListener {
            val intent = Intent(this, ChangePWActivity::class.java)
            intent.putExtra("user_idx",user_idx)
            startActivity(intent)
        }

        //회원탈퇴
        setting_withdrawal.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("정말 탈퇴하시겠습니까?")
            dialog.setCancelable(false);
            dialog.setPositiveButton("네"){ dialog, id ->
                var gson: Gson = GsonBuilder()
                    .setLenient()
                    .create()

                var retrofit = Retrofit.Builder()
                    .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

                var user_withdrawal = retrofit.create(IRetrofit::class.java)
                if (user_idx != null) {
                    user_withdrawal.requestwithdrawal(user_idx).enqueue(object: Callback<Withdrawal> {
                        override fun onResponse(call: Call<Withdrawal>, response: Response<Withdrawal>) {
                            var withdrawal = response.body()
                            if(withdrawal?.message.equals("success")){
                                Toast.makeText(this@SettingActivity,"회원탈퇴가 완료되었습니다.",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SettingActivity, MainActivity::class.java)
                                editor.remove("user_idx")
                                editor.commit()
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()

                            }
                        }

                        override fun onFailure(call: Call<Withdrawal>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }
            dialog.setNegativeButton("아니오"){ dialog, id ->
            }
            dialog.show()
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = this.getSharedPreferences("user",0)
        var passMode = sharedPreferences?.getString("passMode","")
        if(passMode.equals(null) || passMode.equals("")){
            setting_lock.text = "앱 잠금 설정"
        }else{

            setting_lock.text = "앱 잠금 설정 해제"
        }
    }
}
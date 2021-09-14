package com.example.paint_diary.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_app_pass_word.*

class AppPassWordActivity : AppCompatActivity(), View.OnClickListener {
    var passcode : String = "passcode" //비밀번호
    var passcode_check : String = "" // 비밀번호 체크(세팅할 때 비밀번호 확인)
    //비밀번호 자리
    var pass01 : String = ""
    var pass02 : String = ""
    var pass03 : String = ""
    var pass04 : String = ""

    //비밀번호 입력 모드인지 비밀번호 확인 입력 모드인지 체크하는 변수, 0이면 비밀번호 입력 1이면 비밀번호 확인 입력
    var passwordMode = "0"

    //비밀번호 리스트
    val password : ArrayList<String> = ArrayList()
    //비밀번호 확인 리스트
    val password_check : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_pass_word)
        passwordMode = getPassMode()
        var intent = intent
        if(intent.getStringExtra("settingActivity").equals("setting")){
            info_text.text = "비밀번호를 입력해 주세요."
        }else{
            setting()
        }
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
        if(passwordMode == "0"){

            when(v?.id){
                R.id.btn_00 -> {
                    password.add("0")
                }
                R.id.btn_01 -> {
                    password.add("1")
                }
                R.id.btn_02 -> {
                    password.add("2")
                }
                R.id.btn_03 -> {
                    password.add("3")
                }
                R.id.btn_04 -> {
                    password.add("4")
                }
                R.id.btn_05 -> {
                    password.add("5")
                }
                R.id.btn_06 -> {
                    password.add("6")
                }
                R.id.btn_07 -> {
                    password.add("7")
                }
                R.id.btn_08 -> {
                    password.add("8")
                }
                R.id.btn_09 -> {
                    password.add("9")
                }
                R.id.btn_clear -> {
                    password.clear()
                }
                R.id.btn_pre -> {
                    //리스트 갯수가 0보다 커야 삭제, 0 혹은 0보다 작을 경우 지울게 없어서 지우게 되면 앱 터짐
                    if (password.size > 0) {
                        password.remove(password.last())
                    }
                    when (password.size) {
                        1 -> {
                            view_02.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        }
                        2 -> {
                            view_03.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        }
                        3 -> {
                            view_04.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        }
                        else -> {

                        }
                    }
                }
                //else -> {}
            }

            passNumber(password)
        }else{
            when(v?.id){
                R.id.btn_00 -> {
                    password_check.add("0")
                }
                R.id.btn_01 -> {
                    password_check.add("1")
                }
                R.id.btn_02 -> {
                    password_check.add("2")
                }
                R.id.btn_03 -> {
                    password_check.add("3")
                }
                R.id.btn_04 -> {
                    password_check.add("4")
                }
                R.id.btn_05 -> {
                    password_check.add("5")
                }
                R.id.btn_06 -> {
                    password_check.add("6")
                }
                R.id.btn_07 -> {
                    password_check.add("7")
                }
                R.id.btn_08 -> {
                    password_check.add("8")
                }
                R.id.btn_09 -> {
                    password_check.add("9")
                }
                R.id.btn_clear -> {
                    password_check.clear()
                }
                R.id.btn_pre -> {
                    //리스트 갯수가 0보다 커야 삭제, 0 혹은 0보다 작을 경우 지울게 없어서 지우게 되면 앱 터짐
                    if (password_check.size > 0) {
                        password_check.remove(password_check.last())
                    }
                    when (password_check.size) {
                        1 -> {
                            view_02.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        }
                        2 -> {
                            view_03.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        }
                        3 -> {
                            view_04.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        }
                        else -> {

                        }
                    }
                }
                //else -> {}
            }

            passNumber(password_check)
        }

    }

    //키패드를 눌렀을 때 작동하는 메소드
    private fun passNumber(password: ArrayList<String>?) {

       // Log.e("password Size", password?.size.toString())
        if(password == null || password.size == 0){
            view_01.setBackgroundResource(R.drawable.bg_view_gray_oval)
            view_02.setBackgroundResource(R.drawable.bg_view_gray_oval)
            view_03.setBackgroundResource(R.drawable.bg_view_gray_oval)
            view_04.setBackgroundResource(R.drawable.bg_view_gray_oval)
        }else{
            when(password.size){
                1 -> {
                    pass01 = password.get(0)
                    view_01.setBackgroundResource(R.drawable.bg_view_green_oval)
                }
                2 -> {
                    pass02 = password.get(1)
                    view_02.setBackgroundResource(R.drawable.bg_view_green_oval)
                }
                3 -> {
                    pass03 = password.get(2)
                    view_03.setBackgroundResource(R.drawable.bg_view_green_oval)
                }
                4 -> {
                    pass04 = password.get(3)
                    view_04.setBackgroundResource(R.drawable.bg_view_green_oval)
                    if(passwordMode == "0"){
                        passcode = pass01 + pass02 + pass03 + pass04
                        passwordMode = "1"
                        info_text.text = "한번 더 입력해 주세요"
                        view_01.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        view_02.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        view_03.setBackgroundResource(R.drawable.bg_view_gray_oval)
                        view_04.setBackgroundResource(R.drawable.bg_view_gray_oval)
                    }else{
                        passcode_check = pass01 + pass02 + pass03 + pass04
                        if(intent.getStringExtra("settingActivity").equals("setting")){
                            if(passcode.equals(passcode_check)){
                                savePasscode(passcode)
                                finish()
                                Toast.makeText(this,"앱 잠금이 설정되었습니다.",Toast.LENGTH_SHORT).show()
                            }else{
                                info_text.text = "비밀번호가 틀렸습니다."
                                password_check?.clear()
                                val handler = Handler()
                                handler.postDelayed(Runnable {
                                    passNumber(password)
                                }, 100)
                            }
                        }else{
                            matchPassCode(password)
                        }


                    }
                }
            }
        }
    }

    private fun setting(){
        //쉐어드에서 가져온 패스워드 모드가 잠금설정이 아닐 때
        if(getPassMode().equals(null) || getPassMode() == "0"){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //잠금설정이 되어 있을 때
        else{
//            if(passcode.equals(passcode_check)){
//                savePasscode(passcode)
//            }

        }
    }
    private fun matchPassCode(password: ArrayList<String>?) {
        if(getPassCode().equals(passcode_check)){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            info_text.text = "비밀번호가 틀렸습니다."
            password?.clear()
            val handler = Handler()
            handler.postDelayed(Runnable {
                passNumber(password)
            }, 100)
        }
    }


    private fun savePasscode(passcode: String): SharedPreferences.Editor? {
        //쉐어드에 비밀번호와 앱 잠금 상태인지 아닌지 체크
        val sharedPreferences = this.getSharedPreferences("user", 0)
        val editor = sharedPreferences.edit()
        editor.putString("passcode", passcode)
        editor.putString("passMode", passwordMode)
        editor.apply()
        return editor
    }


    private fun getPassCode():String{
        //쉐어드에 비밀번호와 앱 잠금 상태인지 아닌지 체크
        val sharedPreferences = this.getSharedPreferences("user", 0)
        return sharedPreferences.getString("passcode", "")!!
    }
    private fun getPassMode():String{
        //쉐어드에 비밀번호와 앱 잠금 상태인지 아닌지 체크
        val sharedPreferences = this.getSharedPreferences("user", 0)
        return sharedPreferences.getString("passMode", "0").toString()
    }
}
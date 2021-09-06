package com.example.paint_diary.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hanks.passcodeview.PasscodeView
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_passcode.*

class PasscodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)

//        passcode_view.setPasscodeLength(4) //패스워드 길이
//            .setLocalPasscode("1234")
//            .setListener(object : PasscodeView.PasscodeViewListener {
//                override fun onFail() {
//                    //패스워드가 일치하지 않으면
//                    Toast.makeText(this@PasscodeActivity,"실패",Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onSuccess(number: String?) {
//                    //패스워드가 일치하면
//                    var intent = Intent(this@PasscodeActivity,MainActivity::class.java)
//                    startActivity(intent)
//                }
//
//            })

    }
}
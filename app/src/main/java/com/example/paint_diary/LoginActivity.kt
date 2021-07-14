package com.example.paint_diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var login_email : EditText = findViewById(R.id.login_email);
        var login_password : EditText = findViewById(R.id.login_password);
        var login_btn : Button = findViewById(R.id.login_btn);
        var login_find_password : TextView = findViewById(R.id.login_find_password);
        var login_to_signup : TextView = findViewById(R.id.login_to_signup);


        login_btn.setOnClickListener{
            Toast.makeText(this@LoginActivity,"로그인",Toast.LENGTH_SHORT).show()
        }
        login_find_password.setOnClickListener{
            Toast.makeText(this@LoginActivity,"비번찾기",Toast.LENGTH_SHORT).show()
        }
        login_to_signup.setOnClickListener{
            Toast.makeText(this@LoginActivity,"회원가입",Toast.LENGTH_SHORT).show()
        }

    }
}
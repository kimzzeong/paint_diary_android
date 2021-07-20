package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var homeFragment: HomeFragment
    private lateinit var chatFragment: ChatFragment
    private lateinit var mypageFragment: MypageFragment

    companion object {
        const val TAG : String = "로그"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = this.getSharedPreferences("user",0)
        val editor = sharedPreferences.edit()
        Log.d(TAG,"MainActivity = onCreate() called")
        Log.e("TAG", "쉐어드에 저장된 아이디 = " + sharedPreferences?.getString("user_idx", ""))

        bottom_nav.setOnNavigationItemSelectedListener(this)

        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.main_fragment,homeFragment).commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG,"MainActivity = onNavigationItemSelected() called")
        when(item.itemId){
            R.id.bottom_nav_home -> {
                Log.d(TAG,"MainActivity = 홈")
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment,homeFragment).commit()
            }
            R.id.bottom_nav_chat -> {
                Log.d(TAG,"MainActivity = 채팅")
                chatFragment = ChatFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment,chatFragment).commit()
            }
            R.id.bottom_nav_mypage -> {
                Log.d(TAG,"MainActivity = 마이페이지")
                mypageFragment = MypageFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment,mypageFragment).commit()
            }
        }
        return true
    }
}
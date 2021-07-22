package com.example.paint_diary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_join_memebership.*
import kotlinx.android.synthetic.main.activity_profile_modify.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_mypage.*

class ProfileModifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)

        profile_toolbar.inflateMenu(R.menu.profile_save_menu)
        profile_toolbar.setTitleTextColor(Color.BLACK)
        profile_toolbar.setTitle("프로필 수정")


        val cameraPopup = PopupMenu(this,profile_photo)
        menuInflater?.inflate(R.menu.profile_menu,cameraPopup.menu)

        profile_photo?.setOnClickListener {

            cameraPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_camera ->
                        Toast.makeText(this,"카메라", Toast.LENGTH_SHORT).show()
                    R.id.profile_gallery ->
                        Toast.makeText(this,"갤러리", Toast.LENGTH_SHORT).show()
                    R.id.profile_basic ->
                        Toast.makeText(this,"기본", Toast.LENGTH_SHORT).show()
                }
                false
            }

            cameraPopup.show()

        }

    }
}
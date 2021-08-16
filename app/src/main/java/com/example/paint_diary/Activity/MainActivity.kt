package com.example.paint_diary.Activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.paint_diary.Fragment.ChatFragment
import com.example.paint_diary.Fragment.HomeFragment
import com.example.paint_diary.Fragment.MypageFragment
import com.example.paint_diary.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var homeFragment: HomeFragment
    private lateinit var chatFragment: ChatFragment
    private lateinit var mypageFragment: MypageFragment
    var btn: String? = "bottom_nav_home"

    //val editor = sharedPreferences.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("TAG","MainActivity = onCreate() called")
        //Log.e("TAG", "쉐어드에 저장된 아이디 = " + sharedPreferences?.getString("user_idx", ""))

        bottom_nav.setOnNavigationItemSelectedListener(this)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.main_fragment,homeFragment,"home").commit()

    }

    override fun onStart() {
        super.onStart()
        readCheckPermission()
        writeCheckPermission()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("TAG","MainActivity = onNavigationItemSelected() called")
        val sharedPreferences = this.getSharedPreferences("user",0)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        when(item.itemId){
            R.id.bottom_nav_home -> {
                if(!bottom_nav.menu.findItem(R.id.bottom_nav_home).isChecked){
                    btn = "bottom_nav_home"
                    Log.d("TAG","MainActivity = 홈")
                    homeFragment = HomeFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.main_fragment,homeFragment,"home").commit()
                }
            }
            R.id.bottom_nav_chat -> {
                if(!bottom_nav.menu.findItem(R.id.bottom_nav_chat).isChecked){
                    btn = "bottom_nav_chat"
                    Log.d("TAG","MainActivity = 채팅")
                    chatFragment = ChatFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.main_fragment,chatFragment,"chat").commit()
                }
            }
            R.id.bottom_nav_mypage -> {
                if(user_idx != ""){
                    if(!bottom_nav.menu.findItem(R.id.bottom_nav_mypage).isChecked){
                        btn = "bottom_nav_mypage"
                        Log.d("TAG","MainActivity = 마이페이지")
                        mypageFragment = MypageFragment.newInstance()
                        supportFragmentManager.beginTransaction().replace(R.id.main_fragment,mypageFragment,"mypage").commit()
                    }
                }else{//로그인 상태가 아니면 다이얼로그를 이용해 로그인 페이지로 이동

                    //Log.e("TAG", "쉐어드에 저장된 아이디 = " + user_idx)
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage("로그인을 하셔야 이용 가능한 서비스입니다.\n지금 바로 로그인하시겠습니까?")
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("네"){ dialog, id ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    dialog.setNegativeButton("아니오"){ dialog, id ->
                        //아니오 클릭 시 이전 프래그먼트로 이동
                        if(btn.equals("bottom_nav_home")){
                            bottom_nav.setSelectedItemId(R.id.bottom_nav_home)
                        }else if(btn.equals("bottom_nav_chat")){
                            bottom_nav.setSelectedItemId(R.id.bottom_nav_chat)
                        }else if(btn.equals("bottom_nav_mypage")){
                            bottom_nav.setSelectedItemId(R.id.bottom_nav_mypage)
                        }
                    }
                    dialog.show()
                }
            }
        }
        return true
    }
    private fun readCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@MainActivity,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }
    private fun writeCheckPermission() {

        Dexter.withContext(this).withPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@MainActivity,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }
    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("앱 설정에서 권한을 허용해 주세요")

            .setPositiveButton("설정") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}
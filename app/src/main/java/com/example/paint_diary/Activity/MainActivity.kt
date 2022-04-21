package com.example.paint_diary.Activity

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.paint_diary.Fragment.ChatFragment
import com.example.paint_diary.Fragment.HomeFragment
import com.example.paint_diary.Fragment.MypageFragment
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.MyService
import com.example.paint_diary.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var homeFragment: HomeFragment
    private lateinit var chatFragment: ChatFragment
    private lateinit var mypageFragment: MypageFragment
    var btn: String? = "bottom_nav_home"

    var user_idx : String? = null
    var room : ArrayList<String> ?= null

    var gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


    var serviceIsRun : String = MyService::class.java.name

    //val editor = sharedPreferences.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("TAG", "MainActivity = onCreate() called")
        //Log.e("TAG", "쉐어드에 저장된 아이디 = " + sharedPreferences?.getString("user_idx", ""))

        bottom_nav.setOnNavigationItemSelectedListener(this)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, homeFragment, "home").commit()




        val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        user_idx = sharedPreferences?.getString("user_idx", "")

        if(!user_idx!!.isEmpty()){

            requestChatRoom(user_idx!!)
        }
        isServiceRunning(serviceIsRun)

    }

    //서비스가 실행중인지 확인하는 메소드
    private fun isServiceRunning(serviceIsRun: String) : Boolean {


        // 시스템 내부의 액티비티 상태를 파악하는 ActivityManager객체를 생성한다.
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager


        // 리턴값은 List<ActivityManager.RunningServiceInfo>이다. (ActivityManager.RunningServiceInfo의 객체를 담은 List)
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {


            // ActivityManager.RunningServiceInfo의 객체를 통해 현재 실행중인 서비스의 정보를 가져올 수 있다.
            if (serviceIsRun == service.service.className) {
                return true
            }
        }
        return false

    }

    override fun onStart() {
        super.onStart()
        readCheckPermission()
        writeCheckPermission()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("TAG", "MainActivity = onNavigationItemSelected() called")
        val sharedPreferences = this.getSharedPreferences("user", 0)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        when(item.itemId){
            R.id.bottom_nav_home -> {
                if (!bottom_nav.menu.findItem(R.id.bottom_nav_home).isChecked) {
                    btn = "bottom_nav_home"
                    Log.d("TAG", "MainActivity = 홈")
                    homeFragment = HomeFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(
                            R.id.main_fragment,
                            homeFragment,
                            "home"
                    ).commit()
                }
            }
            R.id.bottom_nav_chat -> {
                if (!bottom_nav.menu.findItem(R.id.bottom_nav_chat).isChecked) {
                    btn = "bottom_nav_chat"
                    Log.d("TAG", "MainActivity = 채팅")
                    chatFragment = ChatFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(
                            R.id.main_fragment,
                            chatFragment,
                            "chat"
                    ).commit()
                }
            }
            R.id.bottom_nav_mypage -> {
                if (user_idx != "") {
                    if (!bottom_nav.menu.findItem(R.id.bottom_nav_mypage).isChecked) {
                        btn = "bottom_nav_mypage"
                        Log.d("TAG", "MainActivity = 마이페이지")
                        mypageFragment = MypageFragment.newInstance()
                        supportFragmentManager.beginTransaction().replace(
                                R.id.main_fragment,
                                mypageFragment,
                                "mypage"
                        ).commit()
                    }
                } else {//로그인 상태가 아니면 다이얼로그를 이용해 로그인 페이지로 이동

                    //Log.e("TAG", "쉐어드에 저장된 아이디 = " + user_idx)
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage("로그인을 하셔야 이용 가능한 서비스입니다.\n지금 바로 로그인하시겠습니까?")
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("네") { dialog, id ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    dialog.setNegativeButton("아니오") { dialog, id ->
                        //아니오 클릭 시 이전 프래그먼트로 이동
                        if (btn.equals("bottom_nav_home")) {
                            bottom_nav.setSelectedItemId(R.id.bottom_nav_home)
                        } else if (btn.equals("bottom_nav_chat")) {
                            bottom_nav.setSelectedItemId(R.id.bottom_nav_chat)
                        } else if (btn.equals("bottom_nav_mypage")) {
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

    //room 불러오기
    private fun requestChatRoom(user_idx: String) {

        var api = retrofit.create(IRetrofit::class.java)

        api.requestMyChatRoom(user_idx).enqueue(object : Callback<ArrayList<String>> {
            override fun onResponse(call: Call<ArrayList<String>>, response: Response<ArrayList<String>>) {
                room = response.body()
                Log.e("MainActivity room size", room?.size.toString())
                //서비스가 실행되어 있는 상태 && 로그인이 되어있는 상태일 때 서비스 시작
                if (!isServiceRunning(serviceIsRun) && !user_idx.equals("")) {

                    Log.e("user_idx", user_idx)
                    var intent = Intent(applicationContext, MyService::class.java)
                    intent.putExtra("user_idx", user_idx)
                    intent.putStringArrayListExtra("room_list", room)
                    // intent.putStringArrayListExtra("room_list",room)
                    if (!user_idx.equals("") && !user_idx.isEmpty()) {
                        //서비스 실행
                        startService(intent)
                    }
                }
                Log.e("서비스 상태", isServiceRunning(serviceIsRun).toString())

            }

            override fun onFailure(call: Call<ArrayList<String>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}
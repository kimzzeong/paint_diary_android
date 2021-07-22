package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageFragment : Fragment() {
    companion object {
        const val TAG : String = "로그"
        fun newInstance() : MypageFragment {
            return MypageFragment()
        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        Log.d(TAG, "MypageFragment = onCreate() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MypageFragment = onStart() called")


        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")
        mypage_introduction.setVisibility(View.GONE)
        //if( user_idx != "" ){ //shared에 user_idx가 존재하면=>"로그인 상태라면"
            //retrofit으로 프로필 불러오기
            var gson: Gson = GsonBuilder()
                .setLenient()
                .create()

            var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            var user_profile = retrofit.create(IRetrofit::class.java)
            if (user_idx != null) {
                user_profile.requestProfile(user_idx).enqueue(object: Callback<Profile> {
                    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                        var profile = response.body()
                        if(profile?.user_profile == null){
                            mypage_profile_photo.setImageResource(R.drawable.basic_profile)
                        }else{
                            mypage_profile_photo.setImageResource(R.drawable.ic_baseline_add_24)
                        }
                        mypage_nickname.setText(profile?.user_nickname)
                        if(profile?.user_introduction != null){
                            mypage_introduction.setVisibility(View.VISIBLE)
                            mypage_introduction.setText(profile?.user_introduction)
                        }
                    }

                    override fun onFailure(call: Call<Profile>, t: Throwable) {
                        Log.e("TAG","실패")
                    }

                })
            }

        //}else{//로그인 상태가 아니면 다이얼로그를 이용해 로그인 페이지로 이동
//            Log.e("TAG", "쉐어드에 저장된 아이디 = " + user_idx)
//            val dialog = AlertDialog.Builder(requireActivity())
//            dialog.setMessage("로그인을 하셔야 이용 가능한 서비스입니다.\n지금 바로 로그인하시겠습니까?")
//            dialog.setCancelable(false);
//            dialog.setPositiveButton("네"){ dialog, id ->
//                val intent = Intent(context, LoginActivity::class.java)
//                startActivity(intent)
//            }
//            dialog.setNegativeButton("아니오"){ dialog, id ->
//                //프래그먼트->프래그먼트 이동
//                activity?.supportFragmentManager?.beginTransaction()
//                    ?.replace(R.id.main_fragment,HomeFragment.newInstance())?.commit()
//                //프래그먼트 이동 시 바텀네비게이션 메뉴 활성화
//                val bnv = activity?.findViewById<View>(R.id.bottom_nav) as BottomNavigationView
//                bnv.setSelectedItemId(R.id.bottom_nav_home);
//
//            }
//            dialog.show()
      //  }

    }

    //프래그먼트를 안고 있는 엑티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "MypageFragment = onAttach() called")

    }
    //뷰가 생성 되었을 때
    //프래그먼트와 뷰를 연결시킴
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "MypageFragment = onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //메뉴이름 변경 해야함
        //mypage_toolbar.inflateMenu(R.menu.home_menu)
        mypage_toolbar.setTitleTextColor(Color.WHITE)
        mypage_toolbar.setTitle("마이페이지")

        mypage_profile_modify.setOnClickListener {
            //프래그먼트 -> 액티비티 화면 이동
            val intent = Intent(context, ProfileModifyActivity::class.java)
            startActivity(intent)
//            var intent = Intent(this, RegisterActivity::class.java)
//            startActivityForResult(intent, REQUEST_CODE)
        }

        mypage_setting.setOnClickListener {
            val intent = Intent(context,SettingActivity::class.java)
            startActivity(intent)
        }

    }
}



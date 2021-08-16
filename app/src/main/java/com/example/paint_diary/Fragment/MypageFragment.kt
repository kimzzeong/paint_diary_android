package com.example.paint_diary.Fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.paint_diary.Activity.ProfileModifyActivity
import com.example.paint_diary.Activity.SettingActivity
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.Profile
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MypageFragment : Fragment() {
    var user_nickname :String? = null
    var user_introduction :String? = null
    var profile_photo : String? = null
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
                    user_introduction = profile?.user_introduction
                    user_nickname = profile?.user_nickname
                    if(profile?.user_profile == null){
                        mypage_profile_photo.setImageResource(R.drawable.basic_profile)
                        profile_photo = null;
                    }else{
                        var uriToString : String = profile?.user_profile
                        var uri : Uri = Uri.parse(uriToString)
                        profile_photo = "http://3.36.52.195/profile/"+uri
                        //profile_photo = "/data/user/0/com.example.paint_diary/cache/"+uri
                        Glide.with(activity!!) // context
                            .load(profile_photo) // 이미지 url
                            .into(mypage_profile_photo); // 붙일 imageView
                       // mypage_profile_photo.setImageResource(R.drawable.ic_baseline_add_24)
                    }
                    mypage_nickname.setText(profile?.user_nickname)
                    if(profile?.user_introduction != null){
                        mypage_introduction.setVisibility(View.VISIBLE)
                        mypage_introduction.setText(profile?.user_introduction)
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("실패",t.localizedMessage)
                }

            })
        }

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

            val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
            var user_idx : String? = sharedPreferences?.getString("user_idx", "")
            //프래그먼트 -> 액티비티 화면 이동
            val intent = Intent(context, ProfileModifyActivity::class.java)
            intent.putExtra("user_idx",user_idx)
            intent.putExtra("user_nickname",user_nickname)
            intent.putExtra("user_introduction",user_introduction)
            intent.putExtra("profile_photo",profile_photo)

            Log.e("user_idx", user_idx.toString())
            startActivity(intent)
//            var intent = Intent(this, RegisterActivity::class.java)
//            startActivityForResult(intent, REQUEST_CODE)
        }

        mypage_setting.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
        }

    }
}



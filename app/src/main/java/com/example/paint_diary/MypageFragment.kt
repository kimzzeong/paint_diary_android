package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_mypage.*

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

    }

    //프래그먼트를 안고 있는 엑티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        if( user_idx != "" ){ //shared에 user_idx가 존재하면=>"로그인 상태라면"

            Log.e("TAG", "null 아님"+user_idx)

        }else{//로그인 상태가 아니면 다이얼로그를 이용해 로그인 페이지로 이동
            Log.e("TAG", "쉐어드에 저장된 아이디 = " + user_idx)
            val dialog = AlertDialog.Builder(requireActivity())
            dialog.setMessage("로그인을 하셔야 이용 가능한 서비스입니다.\n지금 바로 로그인하시겠습니까?")
            dialog.setCancelable(false);
            dialog.setPositiveButton("네"){ dialog, id ->
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
            dialog.setNegativeButton("아니오"){ dialog, id ->
                //프래그먼트->프래그먼트 이동
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_fragment,HomeFragment.newInstance())?.commit()
                //프래그먼트 이동 시 바텀네비게이션 메뉴 활성화
                val bnv = activity?.findViewById<View>(R.id.bottom_nav) as BottomNavigationView
                bnv.setSelectedItemId(R.id.bottom_nav_home);

            }
            dialog.show()
        }
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
        //여기에 팝업 정의
        val cameraPopup = PopupMenu(activity,mypage_profile_photo)
        activity?.menuInflater?.inflate(R.menu.profile_menu,cameraPopup.menu)

        mypage_profile_photo?.setOnClickListener {

            cameraPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_camera ->
                        Toast.makeText(activity,"카메라",Toast.LENGTH_SHORT).show()
                    R.id.profile_gallery ->
                        Toast.makeText(activity,"갤러리",Toast.LENGTH_SHORT).show()
                    R.id.profile_basic ->
                        Toast.makeText(activity,"기본",Toast.LENGTH_SHORT).show()
                }
                false
            }

            cameraPopup.show()

        }

        mypage_profile_modify.setOnClickListener {
            //프래그먼트 -> 액티비티 화면 이동
            val intent = Intent(context, ProfileModifyActivity::class.java)
            startActivity(intent)
        }
    }
}



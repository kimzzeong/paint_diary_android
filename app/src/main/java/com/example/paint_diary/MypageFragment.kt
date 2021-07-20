package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        var test : String? = sharedPreferences?.getString("user_idx", "")


        if( test!= null){
            Log.e("TAG", "쉐어드에 저장된 아이디 = " + test)
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("내용")
            builder.setPositiveButton("네"){ dialog, id ->
                //perform some tasks here
                Toast.makeText(activity, "Yes", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("아니오"){ dialog, id ->
                //perform some tasks here
                android.widget.Toast.makeText(activity, "No", android.widget.Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }else{
            //val dialog = AlertDialog.Builder(context!!)
            //dialog.setTitle("title")
//                val intent = Intent(context, LoginActivity::class.java)
//                startActivity(intent)

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
    }
}
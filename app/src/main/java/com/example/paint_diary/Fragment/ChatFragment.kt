package com.example.paint_diary.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {
    companion object {
        const val TAG : String = "로그"
        fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(TAG,"ChatFragment = onCreate() called")
    }

    //프래그먼트를 안고 있는 엑티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"ChatFragment = onAttach() called")
    }

    //뷰가 생성 되었을 때
    //프래그먼트와 뷰를 연결시킴
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"ChatFragment = onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_chat,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //메뉴이름 변경 해야함
        //chat_toolbar.inflateMenu(R.menu.home_menu)
        chat_toolbar.setTitleTextColor(Color.WHITE)
        chat_toolbar.setTitle("채팅")
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.date_pick-> {
//                // navigate to settings screen
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
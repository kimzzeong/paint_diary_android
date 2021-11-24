package com.example.paint_diary.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_diary.Adapter.RoomAdapter
import com.example.paint_diary.Adapter.UserProfileDiaryListAdapter
import com.example.paint_diary.Data.Room
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import java.util.ArrayList

class ChatFragment : Fragment() {
    companion object {
        const val TAG : String = "로그"
        fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }

    lateinit var roomAdapter: RoomAdapter
    var roomList = mutableListOf<Room>()

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

        val activity = activity as AppCompatActivity?


        //메뉴이름 변경 해야함
        //chat_toolbar.inflateMenu(R.menu.home_menu)
        chat_toolbar.setTitleTextColor(Color.WHITE)
        chat_toolbar.setTitle("채팅")

        roomAdapter = RoomAdapter()
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        chatroom_recyclerview.layoutManager = layoutManager


        //var room = Room("테스트 날짜","테스트 메세지","테스트 방 이름","https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201909/10/97737cb1-a4b9-4ee2-a5b7-f784540cfd30.jpg")
        //roomList!!.add(room)
        chatroom_recyclerview.adapter = roomAdapter
        //roomAdapter.roomList = roomList!!
        //Log.e("roomlist",""+roomList?.size)



        roomList.apply {
            add(Room("테스트 날짜", "테스트 메세지", "테스트 방 이름", "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201909/10/97737cb1-a4b9-4ee2-a5b7-f784540cfd30.jpg"))

            roomAdapter.roomList = roomList
            roomAdapter.notifyDataSetChanged()

        }
    }


}
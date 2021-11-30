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
import com.example.paint_diary.Data.ChatRoom
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.ArrayList

class ChatFragment : Fragment() {
    companion object {
        const val TAG : String = "로그"
        fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }

    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    lateinit var roomAdapter: RoomAdapter
    var roomList = mutableListOf<ChatRoom>()

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

        //채팅방 목록 불러옴
        requestChatRoom()




/*        //하드코딩으로 리스트 테스트
        roomList.apply {
            add(Room("테스트 날짜", "테스트 메세지", "테스트 방 이름", "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201909/10/97737cb1-a4b9-4ee2-a5b7-f784540cfd30.jpg"))
            roomAdapter.roomList = roomList
            roomAdapter.notifyDataSetChanged()

        }*/
    }

    //채팅방 전체 리스트 불러오기
    private fun requestChatRoom() {
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        //loaderLayout.visibility = View.VISIBLE
        var chatRoom_request = retrofit.create(IRetrofit::class.java)

        chatRoom_request.requestChatRoom(user_idx!!).enqueue(object : Callback<ArrayList<ChatRoom>>{
            override fun onResponse(call: Call<ArrayList<ChatRoom>>, response: Response<ArrayList<ChatRoom>>) {
                var room = response.body()!!

                chatroom_recyclerview.adapter = roomAdapter
                roomAdapter.roomList = room
                roomAdapter.notifyDataSetChanged()

                Log.e("roomsize",""+room.size)

                if(room.size <= 0){
                    chatroom_textview.visibility = View.VISIBLE
                    chatroom_recyclerview.visibility = View.INVISIBLE
                }else{
                    chatroom_textview.visibility = View.INVISIBLE
                    chatroom_recyclerview.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ArrayList<ChatRoom>>, t: Throwable) {
                Log.e("레트로핏 에러","채팅")
            }

        })

        //loaderLayout.visibility = View.INVISIBLE
    }

}
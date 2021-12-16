package com.example.paint_diary.Fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paint_diary.Activity.ChatActivity
import com.example.paint_diary.Adapter.RoomAdapter
import com.example.paint_diary.Data.Chat2
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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.util.*

class ChatFragment : Fragment() {
    companion object {
        const val TAG : String = "로그"
        fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }

    private val mHandler: Handler? = null
    var socket: Socket? = null
    var sendWriter: PrintWriter? = null
    private val ip = "192.168.56.1" //로컬
    var read = String()

    //private String ip = "3.36.52.195"; //aws ip 주소
    private val port = 8888

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
        Log.d(TAG, "ChatFragment = onCreate() called")
    }

    //프래그먼트를 안고 있는 엑티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ChatFragment = onAttach() called")
    }

    //뷰가 생성 되었을 때
    //프래그먼트와 뷰를 연결시킴
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ChatFragment = onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
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


        chatroom_recyclerview.adapter = roomAdapter

        //채팅방 목록 불러옴
        requestChatRoom()

        //채팅 아이템 클릭 이벤트(채팅방으로 이동)
        roomAdapter.setItemClickListener(object : RoomAdapter.ItemClickListener {
            override fun onClick(
                    view: View,
                    position: Int,
                    room_idx: Int,
                    room_user1: String,
                    room_user2: String,
                    profile_photo: String
            ) {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("room_idx", room_idx)
                intent.putExtra("room_user1", room_user1)
                intent.putExtra("room_user2", room_user2)
                Log.e("Chat Fragment - room_idx", room_idx.toString())
                //Toast.makeText(context,room_idx,Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }


        })

//        object : Thread() {
//            override fun run() {
//                try {
//                    val serverAddr = InetAddress.getByName(ip)
//                    socket = Socket(serverAddr, port)
//                    sendWriter = PrintWriter(socket!!.getOutputStream())
//                    val input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
//                    while (true) {
//                        read = input.readLine()
//                        var msg = arrayOfNulls<String>(10)
//                        msg = read.split(">>".toRegex()).toTypedArray()
//                        println("TTTTTTTT"+read)
//                        if (read != null) {
//                            println("TTTTTTTT"+msg[2])
//                           //mHandler.post(msgUpdate(msg[0], msg[1], msg[2], msg[3], msg[4], msg[5]!!.toInt(), msg[6])) //msg[2]가 room_idx인듯?
//                        }
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }.start()


/*        //하드코딩으로 리스트 테스트
        roomList.apply {
            add(Room("테스트 날짜", "테스트 메세지", "테스트 방 이름", "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/201909/10/97737cb1-a4b9-4ee2-a5b7-f784540cfd30.jpg"))
            roomAdapter.roomList = roomList
            roomAdapter.notifyDataSetChanged()

        }*/
    }

    //채팅방 전체 리스트 불러오기
    private fun requestChatRoom() {

        val my_room_list = ArrayList<ChatRoom>()
        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        //loaderLayout.visibility = View.VISIBLE
        var chatRoom_request = retrofit.create(IRetrofit::class.java)

        chatRoom_request.requestChatRoom(user_idx!!).enqueue(object : Callback<ArrayList<ChatRoom>> {
            override fun onResponse(call: Call<ArrayList<ChatRoom>>, response: Response<ArrayList<ChatRoom>>) {
                var room = response.body()!!


                Log.e("user_idx", user_idx)
                Log.e("room", "" + room.size)

                //코틀린의 for문. room list이고 i가 ChatRoom 객체임
                for (i in room) {
                    if (i.room_user.contains(user_idx)) {

                        Log.e("room_user", i.room_user)
                        my_room_list.add(i)
                    }
                }
                Log.e("my_room_list", "" + my_room_list.size)


                chatroom_recyclerview.adapter = roomAdapter
                roomAdapter.roomList = my_room_list
                roomAdapter.notifyDataSetChanged()

                //채팅방이 없으면 진행중인 채팅방이 없다고 텍스트뷰로 알려줌
                if (my_room_list.size == 0) {
                    chatroom_textview.visibility = View.VISIBLE
                    chatroom_recyclerview.visibility = View.INVISIBLE
                } else {
                    chatroom_textview.visibility = View.INVISIBLE
                    chatroom_recyclerview.visibility = View.VISIBLE
                }

            }

            override fun onFailure(call: Call<ArrayList<ChatRoom>>, t: Throwable) {
                Log.e("레트로핏 에러", "채팅")
            }

        })

    }

//    //여기를 채팅방으로 바꿔야함
//    internal class msgUpdate(
//            private val user_idx: String,
//            private val nickname: String,
//            private val message_room_idx: String,
//            private val msg: String,
//            private val photo: String,
//            private val type: Int,
//            private val date: String
//            ) : Runnable {
//
//        override fun run() {
//            if (message_room_idx == message_room_idx) {
//                val chat = Chat2(msg, user_idx, date, photo, nickname, message_room_idx, type)
//
//            }
//        }
//    }

}
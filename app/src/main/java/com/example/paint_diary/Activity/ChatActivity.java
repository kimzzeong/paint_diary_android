package com.example.paint_diary.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paint_diary.Adapter.ChatAdapter;
import com.example.paint_diary.Data.Chat;
import com.example.paint_diary.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "192.168.56.1"; //로컬
    //private String ip = "3.36.52.195"; //aws ip 주소
    private int port = 8888;

    String UserID = "", user_nickname;
    ImageView chatbutton;
    //  TextView chatView;
    EditText message;
    String sendmsg;
    String read;
    ArrayList<Chat> list = new ArrayList<>();
    RecyclerView recyclerView;
    ChatAdapter adapter;
    String room_idx, profile_photo = "";


    private static final String SHARED_PREF_NAME = "user";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        message = (EditText) findViewById(R.id.message);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        UserID = sharedPreferences.getString("user_idx", String.valueOf(Context.MODE_PRIVATE));
        user_nickname = sharedPreferences.getString("user_nickname", String.valueOf(Context.MODE_PRIVATE));
        chatbutton = (ImageView) findViewById(R.id.chatbutton);

        recyclerView = findViewById(R.id.chatView) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        adapter = new ChatAdapter(list,this) ;
        recyclerView.setAdapter(adapter) ;

        Intent intent = getIntent();
        room_idx = String.valueOf(intent.getIntExtra("room_idx",0));

        Log.e("profile_photo",profile_photo);
        profile_photo = sharedPreferences.getString("profile_photo","없음");
        Log.e("room_idx",room_idx+"");


        new Thread() {
            public void run() {
                try {

                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        read = input.readLine();
                        String[] msg = new String[10];
                        msg = read.split(">>");
                        Log.e("msg size",msg.length+"");
                        Log.e("msg profile",msg[4]);
                        System.out.println("TTTTTTTT"+read);
                        if(read!=null){
                            mHandler.post(new msgUpdate(msg[0],msg[1],msg[2],msg[3],msg[4]));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();

        //내가 보내는 메세지
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                if(sendmsg.isEmpty()){
                    Toast.makeText(ChatActivity.this,"메세지를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {

                                Log.e("msg profile",profile_photo);
                                sendWriter.println(UserID + ">>" +user_nickname +">>" + room_idx + ">>" + sendmsg + ">>" + profile_photo);
                                sendWriter.flush();
                                message.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
    }

    class msgUpdate implements Runnable{
        private String msg;
        private String user_idx;
        private String message_room_idx;
        private String nickname;
        private String photo;
        public msgUpdate(String user_idx, String nickname, String room_idx, String str, String photo) {
            this.msg=str;
            this.user_idx = user_idx;
            this.message_room_idx = room_idx;
            this.nickname = nickname;
            this.photo = photo;
        }

        @Override
        public void run() {

            Date date_now = new Date(System.currentTimeMillis()); // 현재시간을 가져와 Date형으로 저장한다
            // 년월일시분초 14자리 포멧
            SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if(room_idx.equals(message_room_idx)){
                Chat chat = new Chat(msg,user_idx,fourteen_format.format(date_now),photo,nickname);
                list.add(chat);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1); // 제일 최근 채팅으로 포지션 이동
            }

        }
    }


}
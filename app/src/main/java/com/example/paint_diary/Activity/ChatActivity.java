package com.example.paint_diary.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.paint_diary.Data.Chat2;
import com.example.paint_diary.IRetrofit;
import com.example.paint_diary.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "192.168.56.1"; //로컬
    //private String ip = "3.36.52.195"; //aws ip 주소
    private int port = 8888;

    String UserID = "", user_nickname;
    ImageView chatbutton; // 채팅 보내기
    ImageView imagebutton; // 채팅에 이미지 보낼 때 누를 아이콘
    EditText message;
    String sendmsg;
    String read;
    ArrayList<Chat2> list = new ArrayList<>(); // 채팅 리스트
    RecyclerView recyclerView;
    ChatAdapter adapter;
    String room_idx, profile_photo = "";
    Date date_now;
    SimpleDateFormat fourteen_format; //날짜 포맷
    String date;
    String[] photo; //포토 다이얼로그 목록을 위한 배열

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
        chatbutton = findViewById(R.id.chatbutton);
        imagebutton = findViewById(R.id.imagebutton);

        Intent intent = getIntent();
        room_idx = String.valueOf(intent.getIntExtra("room_idx",0));

        recyclerView = findViewById(R.id.chatView) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;


        requestChat(room_idx,this);

        adapter = new ChatAdapter(list,this) ;
        recyclerView.setAdapter(adapter) ;


        Log.e("profile_photo",profile_photo);
        profile_photo = sharedPreferences.getString("profile_photo","없음");
        Log.e("room_idx",room_idx+"");

        //이미지 버튼 클릭 시 카메라, 갤러리 다이얼로그 띄우기
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_photo_Dialog();
            }
        });


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
                            mHandler.post(new msgUpdate(msg[0],msg[1],msg[2],msg[3],msg[4],Integer.parseInt(msg[5]),msg[6]));
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


                                date_now = new Date(System.currentTimeMillis()); // 현재시간을 가져와 Date형으로 저장한다
                                // 년월일시분초 14자리 포멧
                                fourteen_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                date = fourteen_format.format(date_now);

                                Log.e("msg profile",profile_photo);
                                sendWriter.println(UserID + ">>" +user_nickname +">>" + room_idx + ">>" + sendmsg + ">>" + profile_photo + ">>" + 0 + ">>" + date);
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
        private final String msg;
        private final String user_idx;
        private final String message_room_idx;
        private final String nickname;
        private final String photo;
        private final String date;
        private int type;

        public msgUpdate(String user_idx, String nickname, String room_idx, String str, String photo, int type, String date) {
            this.msg=str;
            this.user_idx = user_idx;
            this.message_room_idx = room_idx;
            this.nickname = nickname;
            this.photo = photo;
            this.type = type;
            this.date = date;
        }

        @Override
        public void run() {

            if(room_idx.equals(message_room_idx)){
                Chat2 chat = new Chat2(msg,user_idx,date,photo,nickname,room_idx,type);
                list.add(chat);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1); // 제일 최근 채팅으로 포지션 이동
            }

        }
    }


    //채팅 리스트 불러오기
    public void requestChat(String room_idx, Context context){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        IRetrofit api = retrofit.create(IRetrofit.class);

        Call<ArrayList<Chat2>> call = api.requestChat(room_idx);

        call.enqueue(new Callback<ArrayList<Chat2>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Chat2>> call, @NotNull Response<ArrayList<Chat2>> response) {
                list = response.body();

                adapter = new ChatAdapter(list,context) ;
                recyclerView.setAdapter(adapter) ;

                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1); // 제일 최근 채팅으로 포지션 이동
                Log.e("채팅 리스트 레트로핏","성공");
                //Log.e("채팅 리스트 size",""+list.get(0).getType());
            }

            @Override
            public void onFailure(Call<ArrayList<Chat2>> call, Throwable t) {
                Log.e("채팅 리스트 레트로핏 실패",t.getLocalizedMessage());


            }
        });
    }

    public void show_photo_Dialog() {
        photo = getResources().getStringArray(R.array.photoArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.photoChoice)
                .setItems(R.array.photoArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(photo[which].equals("카메라")){
                            Toast.makeText(ChatActivity.this,photo[which],Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ChatActivity.this,photo[which],Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
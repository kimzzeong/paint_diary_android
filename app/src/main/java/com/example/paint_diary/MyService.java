package com.example.paint_diary;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.paint_diary.Activity.ChatActivity;
import com.example.paint_diary.Activity.PaintActivity;
import com.example.paint_diary.Adapter.ChatAdapter;
import com.example.paint_diary.Data.Chat2;
import com.example.paint_diary.Data.ChatRoom;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService extends Service {

    NotificationManager Notifi_M;
    ServiceThread thread;
    NotificationChannel channel;
    private final String CHANNEL_ID = "channel_id_example_01";
    String user_idx,room_idx;
    String GROUP_KEY_WORK_EMAIL = "com.example.paint_diary";
    Bitmap bitmap; // 비트맵 얻어서 노티의 라지아이콘에 넣기
    ArrayList<String> room = new ArrayList<>();

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public MyService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user_idx = intent.getStringExtra("user_idx");
        room = intent.getStringArrayListExtra("room_list");
        Log.e("서비스 룸 사이즈",room.size()+"");
        channel = new NotificationChannel(CHANNEL_ID,"ㅎㅇ",NotificationManager.IMPORTANCE_HIGH);
        MyServiceHandler handler = new MyServiceHandler();
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notifi_M.createNotificationChannel(channel);
        thread = new ServiceThread(handler,user_idx,room);
        thread.start();
        return START_REDELIVER_INTENT;
    }

    //서비스가 종료될 때 할 작업

//    public void onDestroy() {
//        thread.stopForever();
//        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
//    }

    class MyServiceHandler extends Handler {
        ArrayList<String> room = new ArrayList<>();
        ArrayList<String> room_chatActivity;
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            Intent intent = new Intent(MyService.this, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            /*현재 켜져있는 최상위 액티비티가 뭔지 구하는 코드
            * 현재 액티비티가 채팅방 액티비티면 노티 알람 오지 않게 되어있음.
            * room_idx도 받아서 다른 유저와의 채팅방에서는 알림이 오게 해야함*/
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
            ComponentName componentName= info.get(0).topActivity;
            String activityName = componentName.getShortClassName().substring(1);
            room = bundle.getStringArrayList("room_list");
            Log.e("activity_name",activityName);
            Log.e("핸들러 룸 사이즈",room.size()+"");

            if((!activityName.equals("Activity.ChatActivity") && room.contains(bundle.getString("room_idx")))) {

                if (bundle.getString("room_photo").equals("없음")) { //프로필 사진이 없으면 기본 프로필 사진으로 세팅
                    //drawable to bitmap
                    Drawable drawable = getResources().getDrawable(R.drawable.basic_profile);
                    bitmap = ((BitmapDrawable) drawable).getBitmap();

                } else { //프로필 사진이 있으면 프로필 사진을 LargeIcon으로 세팅

                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(bundle.getString("room_photo"))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    bitmap = resource;
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }

                Notification Notifi = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentTitle(bundle.getString("room_name")) //노티의 제목은 채팅방 이름(=상대 유저 닉네임)
                        .setContentText(bundle.getString("content")) //노티의 내용은 채팅 내용
                        .setSmallIcon(R.drawable.sketching)
                        .setContentIntent(pendingIntent)
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        .setGroupSummary(true)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .build();

                //소리추가
                Notifi.defaults = Notification.DEFAULT_SOUND;

                //알림 소리를 한번만 내도록
                Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                //확인하면 자동으로 알림이 제거 되도록
                //  Notifi.flags = Notification.FLAG_AUTO_CANCEL;

//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//                notificationManager.notify((int)(System.currentTimeMillis()/1000), Notifi);
                Notifi_M.notify(0, Notifi);
//                room_idx = ((ChatActivity)ChatActivity.context).room_idx;
//            Toast.makeText(getApplicationContext(),room_idx,Toast.LENGTH_SHORT).show();}


                //채팅방이 켜저있을 땐 내가 현재 속한 값을 제외한 값에서 메세지가 와야 노티주기
            }else if(activityName.equals("Activity.ChatActivity")){
                room_idx = ((ChatActivity)ChatActivity.context).room_idx;
                room_chatActivity = new ArrayList<>();
                room_chatActivity = room;
                //Toast.makeText(getApplicationContext(),room_idx,Toast.LENGTH_SHORT).show();
                Log.e("채팅방 인덱스",room_idx);
                for (int i = 0; i < room_chatActivity.size(); i++){
                    Log.e("채팅방목록",room_chatActivity.get(i));
                }
                if(room_chatActivity.contains(room_idx)){
                    room_chatActivity.remove(room_idx);
                }
                if(room_chatActivity.contains(bundle.getString("room_idx"))){

                    if(bundle.getString("room_photo").equals("없음")){ //프로필 사진이 없으면 기본 프로필 사진으로 세팅
                        //drawable to bitmap
                        Drawable drawable = getResources().getDrawable(R.drawable.basic_profile);
                        bitmap = ((BitmapDrawable)drawable).getBitmap();

                    }else{ //프로필 사진이 있으면 프로필 사진을 LargeIcon으로 세팅

                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(bundle.getString("room_photo"))
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        bitmap = resource;
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }

                    Notification Notifi = new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                            .setContentTitle(bundle.getString("room_name")) //노티의 제목은 채팅방 이름(=상대 유저 닉네임)
                            .setContentText(bundle.getString("content")) //노티의 내용은 채팅 내용
                            .setSmallIcon(R.drawable.sketching)
                            .setContentIntent(pendingIntent)
                            .setGroup(GROUP_KEY_WORK_EMAIL)
                            .setLargeIcon(bitmap)
                            .build();

                    //소리추가
                    Notifi.defaults = Notification.DEFAULT_SOUND;

                    //알림 소리를 한번만 내도록
                    Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                    //확인하면 자동으로 알림이 제거 되도록
                    //  Notifi.flags = Notification.FLAG_AUTO_CANCEL;

                    Notifi_M.notify( 777 , Notifi);

                }
            }
        }
    }

    //room 불러오기
//    private void requestChatRoom(String user_idx) {
//
//
//        IRetrofit api = retrofit.create(IRetrofit.class);
//
//        Call<ArrayList<String>> call = api.requestMyChatRoom(user_idx);
//
//        call.enqueue(new Callback<ArrayList<String>>() {
//            @Override
//            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
//                room = response.body();
//                room_chatActivity = new ArrayList<>();
//                room_chatActivity = room;
//
//                Log.e("레트로핏 내부 룸 사이즈",room.size()+"");
//                for (int i = 0; i < room.size(); i++){
//                    Log.e("방리스트",room.get(i));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
//
//            }
//        });
//
//    }
}

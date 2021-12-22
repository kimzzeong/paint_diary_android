package com.example.paint_diary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.example.paint_diary.Activity.ChatActivity;
import com.example.paint_diary.Activity.PaintActivity;

public class MyService extends Service {

    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;
    NotificationChannel channel;
    private final String CHANNEL_ID = "channel_id_example_01";


    public MyService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        channel = new NotificationChannel(CHANNEL_ID,"ㅎㅇ",NotificationManager.IMPORTANCE_HIGH);
        myServiceHandler handler = new myServiceHandler();
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notifi_M.createNotificationChannel(channel);
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {


            Intent intent = new Intent(MyService.this, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi = new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.sun)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
          //  Notifi.flags = Notification.FLAG_AUTO_CANCEL;


            Notifi_M.notify( 777 , Notifi);

            //토스트 띄우기
            Toast.makeText(MyService.this, "알림 발생", Toast.LENGTH_LONG).show();
        }
    }

}

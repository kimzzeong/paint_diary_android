package com.example.paint_diary;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.paint_diary.Activity.ChatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServiceThread extends Thread{
    Handler mHandler;
    boolean isRun = true;
    //private String ip = "192.168.56.1"; //로컬
    private String ip = "3.36.52.195"; //aws ip 주소
    public Socket socket;
    private int port = 8888;
    public PrintWriter sendWriter;
    String read;
    String user_idx;
    ArrayList<String> room = new ArrayList<>();
    public InetAddress serverAddr;
    public BufferedReader input;

    public ServiceThread(Handler handler, String user_idx, ArrayList<String> room){
        this.mHandler = handler;
        this.user_idx = user_idx;
        this.room = room;
    }


    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        //반복적으로 수행할 작업을 한다.
        try {
            serverAddr = InetAddress.getByName(ip);
            socket = new Socket(serverAddr, port);
            sendWriter = new PrintWriter(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true){

                read = input.readLine();
                String[] msg = new String[10];
                msg = read.split(">>");
                Log.e("msg size",msg.length+"");
                Log.e("msg profile",msg[4]);
                System.out.println("TTTTTTTT"+read);
                if(read!=null){
                    Message message = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("room_list",room);
                    bundle.putString("content",msg[3]);
                    bundle.putString("room_idx",msg[2]);
                    bundle.putString("room_name",msg[1]);
                    bundle.putString("room_photo",msg[4]);
                    message.setData(bundle);
                    if(!user_idx.equals(msg[0])){ //내가 보낸 채팅은 노티X
                        mHandler.sendMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

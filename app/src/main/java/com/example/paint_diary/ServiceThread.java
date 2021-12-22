package com.example.paint_diary;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ServiceThread extends Thread{
    Handler mHandler;
    boolean isRun = true;
    private String ip = "192.168.56.1"; //로컬
    Socket socket;
    private int port = 8888;
    PrintWriter sendWriter;
    String read;

    public ServiceThread(Handler handler){
        this.mHandler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        //반복적으로 수행할 작업을 한다.
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
                    mHandler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                    try{
                        Thread.sleep(5000); //10초씩 쉰다.
                    }catch (Exception e) {}


                    //mHandler.post(new msgUpdate(msg[0],msg[1],msg[2],msg[3],msg[4],Integer.parseInt(msg[5]),msg[6]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

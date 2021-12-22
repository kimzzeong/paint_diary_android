package com.example.paint_diary

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.paint_diary.Fragment.HomeFragment

class NotiService : Service() {
    var mp : MediaPlayer? = null
    companion object {
        const val TAG: String = "로그"
        fun newInstance(): NotiService {
            return NotiService()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mp = MediaPlayer.create(this,R.raw.wanna)
        mp?.isLooping = true //반복재생
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mp?.start() // 노래 시작
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()

        mp?.stop()
    }

}
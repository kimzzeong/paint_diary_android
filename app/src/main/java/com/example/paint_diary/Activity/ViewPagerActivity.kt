package com.example.paint_diary.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.paint_diary.Adapter.ViewPagerAdapter
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        var intent = intent

        if(intent != null){
            Log.e("url",intent.getStringExtra("url")!!)
        }

        chat_viewpager.adapter = ViewPagerAdapter(getImageList() )
        chat_viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }


    private fun getImageList() : ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.secret, R.drawable.basic_profile,R.drawable.sun)
    }

}
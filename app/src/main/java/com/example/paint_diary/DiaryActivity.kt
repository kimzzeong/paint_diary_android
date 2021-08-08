package com.example.paint_diary

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.User.initUsers
import com.example.paint_diary.databinding.ActivityDiaryBinding


class DiaryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDiaryBinding
    var bitmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val arr = intent.getByteArrayExtra("image")
        bitmap = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)

        binding.image.setImageBitmap(bitmap)

        initUsers()
        val spinnerAdapter = SpinnerAdapter(this, com.example.paint_diary.R.layout.custom_spinner_adapter, User.getUserArrayList())
        binding.spinnerWeather.setAdapter(spinnerAdapter)
    }
}
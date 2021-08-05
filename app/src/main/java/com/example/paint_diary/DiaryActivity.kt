package com.example.paint_diary

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.paint_diary.databinding.ActivityDiaryBinding
import com.example.paint_diary.databinding.ActivityPaintBinding

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
    }
}
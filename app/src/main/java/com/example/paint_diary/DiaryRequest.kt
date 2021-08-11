package com.example.paint_diary

data class DiaryRequest(
    val user_idx : Int,
    val diary_title:String,
    val diary_painting:String
    )
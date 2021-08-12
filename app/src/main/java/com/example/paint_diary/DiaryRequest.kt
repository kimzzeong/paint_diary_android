package com.example.paint_diary

data class DiaryRequest(
    val user_idx : Int,
    val diary_idx : Int,
    val diary_title:String,
    val user_nickname:String,
    val diary_painting:String
    )
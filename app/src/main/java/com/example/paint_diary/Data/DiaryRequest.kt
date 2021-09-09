package com.example.paint_diary.Data

data class DiaryRequest(
    val diary_writer : Int,
    val diary_idx : Int,
    val diary_secret : Int,
    val diary_like_count : Int,
    val diary_comment_count : Int,
    val diary_title:String,
    val user_nickname:String,
    val diary_date:String,
    val diary_painting:String
    )
package com.example.paint_diary

data class DiaryInfoPage (
    val diary_title: String,
    val diary_idx: Int,
    val message: String,
    val diary_content: String,
    val diary_nickname: String,
    val diary_painting: String,
    val diary_weather: Int,
    val diary_range: Int,
    val diary_secret: Int,
    val diary_date: String,
    val user_idx: String,
    val user_profile: String,
    val user_intro: String
)
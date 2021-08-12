package com.example.paint_diary

data class DiaryInfo (
    val diary_idx: Int,
    val message: String,
    val user_idx: String,
    val profilePhoto: String?
)
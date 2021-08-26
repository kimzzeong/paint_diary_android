package com.example.paint_diary.Data

data class ProfileChange (
    val status: Int,
    val message: String,
    val user_idx: String,
    val profilePhoto: String?
)
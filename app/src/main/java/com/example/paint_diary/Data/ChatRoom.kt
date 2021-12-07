package com.example.paint_diary.Data

data class ChatRoom (
    val room_idx: Int,
    val room_user: String,
    val room_name: String,
    val room_datetime: String?,
    val room_photo: String?,
    val message : String
)
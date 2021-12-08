package com.example.paint_diary.Data

data class Chat2(// 채팅 내용
        val chat_content: String, //채팅 유저
        val chat_user: String, // 채팅 보낸 시간
        val chat_dateTime: String, // 프로필사진
        val chat_profile_photo: String, // 유저 닉네임
        val chat_nickname: String, // 채팅 타입(텍스트인지 이미지인지)
        val room_idx: String, // 채팅 타입(텍스트인지 이미지인지)
        val chat_type: Int
        )
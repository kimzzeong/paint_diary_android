package com.example.paint_diary

data class CommentsList (
    val message: String,
    val comment_content: String,
    val comment_profile: String,
    val comment_nickname: String,
    val diary_idx: Int,
    val comment_writer: Int,
    val comment_status: Int,
    val comment_datetime: String
)
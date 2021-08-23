package com.example.paint_diary

data class DiaryList(
    val diary_writer : Int,
    val diary_idx : Int,
    val diary_secret : Int,
    val diary_title:String,
    val user_nickname:String,
    val diary_painting:String,
    val diary_date:String,
    val type : Int
    ){
    companion object {
        const val DATE_TYPE = 0
        const val DIARY_TYPE = 1
    }
}
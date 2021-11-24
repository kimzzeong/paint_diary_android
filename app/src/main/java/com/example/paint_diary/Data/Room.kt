package com.example.paint_diary.Data

 class Room {

     var room_datetime: String
     var message: String
     var room_name: String
     var room_profilePhoto: String

     constructor(_room_datetime: String, _message: String, _room_name: String, _room_profilePhoto: String) {
         room_datetime = _room_datetime
         message = _message
         room_name = _room_name
         room_profilePhoto = _room_profilePhoto
     }
 }


package com.example.paint_diary.Data;

import java.net.Socket;

public class ChatUser {

    private int user_idx; 			// Unique ID
    private Room room; 		// 유저가 속한 룸이다.
    private Socket sock; 		// 소켓 object
    private String nickName;	// 닉네임

}

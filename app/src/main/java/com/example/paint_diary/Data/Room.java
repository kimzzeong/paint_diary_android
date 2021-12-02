package com.example.paint_diary.Data;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int room_idx; // 룸 ID
    private List userList; //유저 리스트
    private String roomName; // 방 이름


    public int getRoom_idx() {
        return room_idx;
    }

    public void setRoom_idx(int room_idx) {
        this.room_idx = room_idx;
    }

    public List getUserList() {
        return userList;
    }

    public void setUserList(List userList) {
        this.userList = userList;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }





}

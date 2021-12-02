package com.example.paint_diary.Data;

public class Chat {

    private String content; // 채팅 내용
    private String user_idx; //채팅 유저
    private String dateTime; // 채팅 보낸 시간
    private String profile_photo; // 프로필사진
    private String nickname; // 유저 닉네임

    public Chat(String content,String user_idx, String dateTime, String profile_photo, String nickname){
        this.content = content;
        this.user_idx = user_idx;
        this.dateTime = dateTime;
        this.profile_photo = profile_photo;
        this.nickname = nickname;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_idx() {
        return user_idx;
    }

    public void setUser_idx(String user_idx) {
        this.user_idx = user_idx;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

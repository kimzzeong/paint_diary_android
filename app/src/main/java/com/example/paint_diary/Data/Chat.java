package com.example.paint_diary.Data;

public class Chat {

    private String chat_content; // 채팅 내용
    private String chat_user; //채팅 유저
    private String chat_dateTime; // 채팅 보낸 시간
    private String chat_profile_photo; // 프로필사진
    private String chat_nickname; // 유저 닉네임
    private int chat_type; // 채팅 타입(텍스트인지 이미지인지)

    public Chat(String content,String user_idx, String dateTime, String profile_photo, String nickname, int type){
        this.chat_content = content;
        this.chat_user = user_idx;
        this.chat_dateTime = dateTime;
        this.chat_profile_photo = profile_photo;
        this.chat_nickname = nickname;
        this.chat_type = type;

    }

    public String getContent() {
        return chat_content;
    }

    public void setContent(String content) {
        this.chat_content = content;
    }

    public String getUser_idx() {
        return chat_user;
    }

    public void setUser_idx(String user_idx) {
        this.chat_user = user_idx;
    }

    public String getDateTime() {
        return chat_dateTime;
    }

    public void setDateTime(String dateTime) {
        this.chat_dateTime = dateTime;
    }

    public String getProfile_photo() {
        return chat_profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.chat_profile_photo = profile_photo;
    }

    public String getNickname() {
        return chat_nickname;
    }

    public void setNickname(String nickname) {
        this.chat_nickname = nickname;
    }

    public int getType() {
        return chat_type;
    }

    public void setType(int type) {
        this.chat_type = type;
    }
}

package com.example.paint_diary;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private static ArrayList<User> userArrayList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public User(String id, String name){
        this.id = id;
        this.name = name;
    }

    public static void initUsers(){
        User user1 = new User("0","해");
        userArrayList.add(user1);
        User user2 = new User("1","구름");
        userArrayList.add(user2);
        User user3 = new User("2","달");
        userArrayList.add(user3);
        User user4 = new User("3","비");
        userArrayList.add(user4);
        User user5 = new User("4","눈");
        userArrayList.add(user5);
    }

    public int getImage(){
        switch(getId()){
            case  "0":
                return R.drawable.sun;
            case  "1":
                return R.drawable.cloudy;
            case  "2":
                return R.drawable.moon;
            case  "3":
                return R.drawable.rainy;
            case  "4":
                return R.drawable.snowflake;
        }
        return R.drawable.sun;
    }

    public static ArrayList<User> getUserArrayList(){
        return userArrayList;
    }
}

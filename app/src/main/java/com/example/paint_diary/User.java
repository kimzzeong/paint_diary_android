package com.example.paint_diary;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;

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
}

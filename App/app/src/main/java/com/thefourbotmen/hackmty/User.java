package com.thefourbotmen.hackmty;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    public int user_id;
    @SerializedName("name")
    public String name;
    @SerializedName("is_active")
    public boolean is_active;

    public User(){
        //
    }

    public User(int user_id, String name, boolean is_active){
        this.user_id = user_id;
        this.name = name;
        this.is_active = is_active;
    }

}

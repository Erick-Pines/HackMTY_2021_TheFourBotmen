package com.example.near2;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    public int user_id;
    @SerializedName("name")
    public String name;
    @SerializedName("is_active")
    public boolean is_active;
    @SerializedName("is_infected")
    public boolean is_infected;

    public User(){
        //
    }

    public User(int user_id, String name, boolean is_active, boolean is_infected){
        this.user_id = user_id;
        this.name = name;
        this.is_active = is_active;
        this.is_infected = is_infected;
    }

}

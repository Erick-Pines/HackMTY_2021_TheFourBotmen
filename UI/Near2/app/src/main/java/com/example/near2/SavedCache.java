package com.example.near2;

import android.content.Context;
import android.content.SharedPreferences;

public class SavedCache {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SavedCache(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("sdfile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("user_id"))
            saveUserId(-1);

        if(!sharedPreferences.contains("name"))
            saveUserName("");

        if(!sharedPreferences.contains("is_infected"))
            saveUserInfected(false);

        if(!sharedPreferences.contains("is_active"))
            saveUserActive(false);

    }

    public void saveUserId(int user_id) {
        editor.putInt("user_id", user_id);
        editor.commit();
    }

    public void saveUserName(String name) {
        editor.putString("name", name);
        editor.commit();
    }

    public void saveUserInfected(boolean isInfected) {
        editor.putBoolean("is_infected", isInfected);
        editor.commit();
    }

    public void saveUserActive(boolean isActive) {
        editor.putBoolean("is_active", isActive);
        editor.commit();
    }

    public int getUserId() {
        return sharedPreferences.getInt("user_id", -1);
    }

    public String getUserName() {
        return sharedPreferences.getString("name", "");
    }

    public boolean isUserInfected() {
        return sharedPreferences.getBoolean("is_infected", false);
    }

    public boolean isUserActive() {
        return sharedPreferences.getBoolean("is_active", false);
    }

}

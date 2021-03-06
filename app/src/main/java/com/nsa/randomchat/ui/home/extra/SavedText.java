package com.nsa.randomchat.ui.home.extra;

import android.content.Context;
import android.content.SharedPreferences;

import com.nsa.randomchat.R;


public class SavedText {
    SharedPreferences sharedpreferences;
    Context context;
    public static String SPLASH_DONE="splash_done";
    public static String USER_UID="user_uid";
    public static String ROOM_ID="room_id";
    public static String ROOM_USER_ID="room_user_id";

    public SavedText(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }
    public void setText(String key,String text){
        sharedpreferences.edit().putString(key,text).apply();
    }
    public void setBoolean(String key,boolean b){
        sharedpreferences.edit().putBoolean(key,b).apply();
    }
    public boolean getBoolean(String key){
        return sharedpreferences.getBoolean(key,false);
    }
    public String getText(String key){
        return sharedpreferences.getString(key,"");
    }
    public void remove(String key){
        sharedpreferences.edit().remove(key).commit();
    }

}

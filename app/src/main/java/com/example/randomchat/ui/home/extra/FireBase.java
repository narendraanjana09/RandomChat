package com.example.randomchat.ui.home.extra;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBase {
   private FirebaseDatabase database;
    private DatabaseReference appRef,userRef, userRoomRef,roomRef,chatRoomRef;


    public FireBase(){
        database = FirebaseDatabase.getInstance();
        appRef=database.getReference("random_chat_app");
        userRef=appRef.child("users");
        roomRef =appRef.child("room");
        chatRoomRef =appRef.child("chat_room");
        userRoomRef=userRef.child("userRoom");
    }

    public DatabaseReference getChatRoomRef() {
        return chatRoomRef;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getAppRef() {
        return appRef;
    }

    public DatabaseReference getUserRef() {
        return userRef;
    }

    public DatabaseReference getUserRoomRef() {
        return userRoomRef;
    }

    public DatabaseReference getRoomRef() {
        return roomRef;
    }
}

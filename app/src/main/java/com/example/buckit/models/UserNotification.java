package com.example.buckit.models;

public class UserNotification {

    private String mMessage;
    private Boolean mSeen;
    private Integer mType;

    public UserNotification(String message, Integer type){
        mMessage = message;
        mType = type;
        mSeen = false;
    }

    public void markAsSeen(){
        mSeen = true;
    }

    public String getMessage(){
        return mMessage;
    }

    public Integer getType(){
        return mType;
    }

    public Boolean isSeen(){
        return mSeen;
    }


}

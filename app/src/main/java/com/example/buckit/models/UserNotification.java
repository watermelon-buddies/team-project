package com.example.buckit.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("UserNotification")
public class UserNotification  extends ParseObject {

    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TO_NOTIFY = "toNotify";
    public static final String KEY_TYPE = "type";
    public static final String KEY_IS_SEEN = "isSeen";



    public UserNotification(){

    }


    public String getMessage() {return getString(KEY_MESSAGE);}

    public void setMessage(String message){ put(KEY_MESSAGE, message); }

    public String getToNotify() {return getString(KEY_TO_NOTIFY); }

    public void setToNotify(String user) { put(KEY_TO_NOTIFY, user); }

    public String getType(){return getString(KEY_TYPE);}

    public void setType(String type) {put(KEY_TYPE, type);}

    public Boolean isSeen() {return getBoolean(KEY_IS_SEEN);}

    public void setSeen(boolean status){put(KEY_IS_SEEN, status);}


    public static class Query extends ParseQuery<UserNotification> {


        public Query() {
            super(UserNotification.class);
        }

    }

}

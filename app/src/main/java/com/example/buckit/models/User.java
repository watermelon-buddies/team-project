package com.example.buckit.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_NOTIFICATIONS = "userNotifications";
    public static final String KEY_PROFILE_PICTURE= "profilePic";
    public static final String KEY_CATEGORIES_SELECTED = "catSelected";
    public static final String KEY_EVENTS_RSVP = "eventsRSVP";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_EVENT_RADIUS = "eventRadius";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_PASSWORD = "password";

    public User(){ }

    /* TODO - Modify for array types */

    public void setUsername(String username){ put(KEY_USERNAME, username); }

    public void setPassword(String password){ put(KEY_PASSWORD, password); }


    public void setFriends(String name){
        put(KEY_FRIENDS, name);
    }

    public ParseFile getProfilePic() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePic(ParseFile profilePic){
        put(KEY_PROFILE_PICTURE, profilePic);
    }

    public void setDeviceId(String deviceId){put(KEY_DEVICE_ID, deviceId); }

    public String getDeviceId(){return getString(KEY_DEVICE_ID);}


    public String getCategories() {
        return getString(KEY_CATEGORIES_SELECTED);
    }

    public void setCategories(String categories){
        put(KEY_CATEGORIES_SELECTED, categories);
    }

    public String getEventsRsvpd() {
        return getString(KEY_EVENTS_RSVP);
    }

    public void setEventsRsvpd(String events){
        put(KEY_EVENTS_RSVP, events);
    }

    public String getUsername() { return getString(KEY_USERNAME); }

    public String getUserFirstName() {
        String name = getString(KEY_USERNAME);
        return name.substring(0, name.indexOf(" "));
    }

    public JSONArray getFriends() {return getJSONArray(KEY_FRIENDS);}


    public static class Query extends ParseQuery<User> {


        public Query() {
            super(User.class);
        }

        public Query getTop(){
            setLimit(20);
            return this;
        }

        public Query withFriends(){
            include(KEY_FRIENDS);
            return this;
        }


    }
}

package com.example.buckit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;


@ParseClassName("UserInvite")

public class UserInvite extends ParseObject {

    public static final String KEY_EVENT_TITLE = "eventTitle";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_MEET_TIMES = "meetTimes";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_INVITED = "invited";
    public static final String KEY_ACCEPTED = "accepted";
    public static final String KEY_DURATION = "duration";

    public UserInvite(){

    }

    public String getTitle() {return getString(KEY_EVENT_TITLE);}

    public void setTitle(String title){ put(KEY_EVENT_TITLE, title); }

    public String getLocation() {return getString(KEY_LOCATION); }

    public void setLocation(String location) { put(KEY_LOCATION, location); }

    public JSONArray getMeetTimes() {return getJSONArray(KEY_MEET_TIMES); }

    public void setMeetTimes(ArrayList<String> meetTimes){ put(KEY_MEET_TIMES, meetTimes); }

    public ParseUser getCreator() { return getParseUser(KEY_CREATOR); }

    public void setCreator(ParseUser creator) { put(KEY_CREATOR, creator); }

    public ParseUser getInvited() {return getParseUser(KEY_INVITED); }

    public Boolean hasAccepted() {return getBoolean(KEY_ACCEPTED);}

    public void setDuration(int duration) {put(KEY_DURATION, duration);}

    public Integer getDuration() {return getNumber(KEY_DURATION).intValue();}

    public void setAccepted() {put(KEY_ACCEPTED, true);}

    public void setInvited(ParseUser invited) { put(KEY_INVITED, invited); }


    public static class Query extends ParseQuery<UserInvite> {


        public Query() {
            super(UserInvite.class);
        }

        public UserInvite.Query getTop(){
            setLimit(20);
            return this;
        }

        public UserInvite.Query withInvited(){
            include(KEY_INVITED);
            return this;
        }

        public UserInvite.Query withAccepted(){
            include(KEY_ACCEPTED);
            return this;
        }
    }


}

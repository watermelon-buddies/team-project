package com.example.buckit.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@ParseClassName("bucket_list")
public class Bucketlist extends ParseObject {

    public static final String KEY_NAME = "list_name";
    public static final String KEY_USER = "user";
    public static final String KEY_DEADLINE = "deadline";
    public static final String KEY_ACHIEVED = "achieved";
    public static final String KEY_CATEGORY = "category";

    public Bucketlist(){

    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public Boolean getAchieved () {return getBoolean(KEY_ACHIEVED); }

    public void setAchieved (Boolean achieved) { put (KEY_ACHIEVED, achieved); }

    public String getCategory() { return getString(KEY_CATEGORY); }

    public void setCategory(String category) { put(KEY_CATEGORY, category); }

    public Date getDeadline () {return getDate(KEY_DEADLINE); }

    public void setDeadline (Date deadline) {put (KEY_DEADLINE, deadline); }

/*    public JSONArray getElements() { return getJSONArray(KEY_ELEMENTS);}*/

    public String changeDateToSimpleFormat(String strDate) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d = (Date)formatter.parse(strDate);

        SimpleDateFormat fmtOut = new SimpleDateFormat("MM/dd/yyyy");
        return fmtOut.format(d);
    }

    public static class Query extends ParseQuery<Bucketlist> {


        public Query() {
            super(Bucketlist.class);
        }

        public Query getTop(){
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }
    }
}

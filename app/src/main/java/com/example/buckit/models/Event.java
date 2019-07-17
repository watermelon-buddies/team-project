package com.example.buckit.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {

    public String title;
    public String startTime;
    public String endTime;
    public String imageUrl;
    public String eventUrl;

    public Event(JSONObject object) throws JSONException {
        title = object.getJSONObject("name").getString("text");
        startTime = object.getJSONObject("start").getString("local");
        endTime = object.getJSONObject("end").getString("local");
        imageUrl = object.getJSONObject("logo").getJSONObject("original").getString("url");
        eventUrl = object.getString("url");
    }

    public String getTitle(){
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEventUrl() {
        return eventUrl;
    }

}

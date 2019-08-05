package com.example.buckit.models;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormatSymbols;
import org.mortbay.jetty.Server;

import java.io.StringWriter;

public class Event {

    // Gets event JSON Object and stores it accordingly

    public String title;
    public String startTime;
    public String endTime;
    public String imageUrl;
    public String eventUrl;
    public String description;

    public Event(JSONObject object) throws JSONException {
        title = object.getJSONObject("name").getString("text");
        startTime = object.getJSONObject("start").getString("local");
        endTime = object.getJSONObject("end").getString("local");
        imageUrl = object.getJSONObject("logo").getJSONObject("original").getString("url");
        eventUrl = object.getString("url");
        description = object.getJSONObject("description").getString("text");
    }


    public String getTitle(){
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDate() {
        int month = Integer.parseInt(startTime.substring(5,7));
        String monthName = new DateFormatSymbols().getMonths()[month-1];
        return monthName+" "+startTime.substring(8,10)+", "+startTime.substring(0,4);
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

    public String getDescription() {
        return description.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
    }
}

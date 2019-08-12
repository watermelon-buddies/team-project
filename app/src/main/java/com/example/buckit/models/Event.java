package com.example.buckit.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    // Gets event JSON Object and stores it accordingly

    public String title;
    public String startTime;
    public String endTime;
    public String imageUrl;
    public String eventUrl;
    public String description;
    public Boolean addedToCal;

    public Event(JSONObject object) throws JSONException {
        title = object.getJSONObject("name").getString("text");
        startTime = object.getJSONObject("start").getString("local");
        endTime = object.getJSONObject("end").getString("local");
        imageUrl = object.getJSONObject("logo").getJSONObject("original").getString("url");
        eventUrl = object.getString("url");
        description = object.getJSONObject("description").getString("text");
        addedToCal = false;
    }

    public void setAddedToCal(){
        addedToCal = true;
    }

    public Boolean getAddedToCal(){
        return addedToCal;
    }


    public String getTitle(){
        return title;
    }

    public String getStartTime() {
        return changeTo12HourFormat(startTime.substring(11,19));
    }

    public String getOriginalStartTime() { return startTime;}

    public String getOriginalEndTime() {return endTime;}

    public String getDate() {
        int month = Integer.parseInt(startTime.substring(5,7));
        String monthName = new DateFormatSymbols().getMonths()[month-1];
        return monthName+" "+startTime.substring(8,10)+", "+startTime.substring(0,4);
    }

    public String getEndTime() {
        return changeTo12HourFormat(endTime.substring(11,19));
    }


    public String changeTo12HourFormat (String time) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        DateFormat outputformat = new SimpleDateFormat("hh:mm aa");
        Date date = null;
        String formattedTime = null;
        try{
            //Conversion of input String to date
            date= df.parse(time);
            //old date format to new date format
            formattedTime = outputformat.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
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

package com.example.buckit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.buckit.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectTime extends AppCompatActivity {

    ArrayList<String> creatorMeetTimes;
    private HashMap<String, Integer> userEvents;
    JSONArray sentTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        String meetTimes = getIntent().getStringExtra("InviteTimes");
        try{
            sentTimes = new JSONArray(meetTimes);
        } catch(JSONException e){
            e.printStackTrace();
        }
        userEvents = (HashMap<String, Integer>) getIntent().getSerializableExtra("userCal");
        creatorMeetTimes = new ArrayList<>();
        for(int i = 0; i < sentTimes.length(); i++){
            try {
                creatorMeetTimes.add(sentTimes.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        findMeetTimes();
    }

    private void findMeetTimes(){
        Log.d("beforeRemove", String.valueOf(creatorMeetTimes.size()));
        for(int i = 0; i < creatorMeetTimes.size(); i++){
            String currTime = creatorMeetTimes.get(i);
            if(userEvents.containsKey(currTime)){
                for(int j = 0; j < userEvents.get(currTime) - 1; j++){
                    if(i < creatorMeetTimes.size()){
                        creatorMeetTimes.remove(i);
                    }
                }
                if(i == creatorMeetTimes.size()){
                    creatorMeetTimes.add("break");
                } else {
                    creatorMeetTimes.set(i, "break");
                }

            }
        }
        for(String time : creatorMeetTimes){
            Log.d("check", time);
        }
        Log.d("after remove", String.valueOf(creatorMeetTimes.size()));
    }

}


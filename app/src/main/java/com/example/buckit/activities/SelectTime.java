package com.example.buckit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.buckit.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectTime extends AppCompatActivity {

    ArrayList<String> finalMeetTimes;
    private HashMap<String, Integer> userEvents;
    JSONArray sentTimes;
    ListView lvMeetTimes;
    ArrayAdapter<String> meetTimesAdapter;
    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        String meetTimes = getIntent().getStringExtra("InviteTimes");
        duration = getIntent().getIntExtra("duration", 0);
        try {
            sentTimes = new JSONArray(meetTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userEvents = (HashMap<String, Integer>) getIntent().getSerializableExtra("userCal");
        finalMeetTimes = new ArrayList<>();
        for (int i = 0; i < sentTimes.length(); i++) {
            try {
                finalMeetTimes.add(sentTimes.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        findMeetTimes();
        organizeInRanges();
        meetTimesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalMeetTimes);
        lvMeetTimes = findViewById(R.id.lvMeetTimes);
        lvMeetTimes.setAdapter(meetTimesAdapter);

    }

    private void findMeetTimes() {
        Log.d("beforeRemove", String.valueOf(finalMeetTimes.size()));
        for (int i = 0; i < finalMeetTimes.size(); i++) {
            String currTime = finalMeetTimes.get(i);
            if (userEvents.containsKey(currTime)) {
                for (int j = 0; j < userEvents.get(currTime) - 1; j++) {
                    if (i < finalMeetTimes.size()) {
                        finalMeetTimes.remove(i);
                    }
                }
                if (i == finalMeetTimes.size()) {
                    finalMeetTimes.add("break");
                } else {
                    finalMeetTimes.set(i, "break");
                }

            }
        }
        for (String time : finalMeetTimes) {
            Log.d("check", time);
        }
        Log.d("after remove", String.valueOf(finalMeetTimes.size()));
    }

    private void organizeInRanges() {
        ArrayList<String> bla = removedNotEnoughTime();
        finalMeetTimes.add("break");
        Log.d("ah", finalMeetTimes.toString());

    }


    private ArrayList<String> removedNotEnoughTime() {
        ArrayList<String> withTimesRemoved = new ArrayList<>();
        for (int i = 0; i < finalMeetTimes.size(); i++) {
            if (finalMeetTimes.get(i).equals("break")) {
                finalMeetTimes.remove(i);
                i--;
                if(!withTimesRemoved.get(withTimesRemoved.size() - 1).equals("rangeBreak")){
                    withTimesRemoved.add("rangeBreak");
                }
            } else if (finalMeetTimes.indexOf("break") < 0 || finalMeetTimes.indexOf("break") - i + 1 >= duration) {
                withTimesRemoved.add(finalMeetTimes.get(i));

            }
        }
        return withTimesRemoved;
    }

}


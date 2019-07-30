package com.example.buckit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.buckit.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SelectTime extends AppCompatActivity {

    ArrayList<String> finalMeetTimes;
    ArrayList<ArrayList<String>> ranges;
    private HashMap<String, Integer> userEvents;
    JSONArray sentTimes;
    ListView lvMeetTimes;
    ArrayAdapter<String> meetTimesAdapter;
    int duration;
    Button btnOption1, btnOption2, btnOption3, btnOption4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        ranges = new ArrayList<>();
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
        try {
            organizeInRanges();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    private void organizeInRanges() throws ParseException {
        finalMeetTimes = removedNotEnoughTime();
        createSeparateArrayLists();
        setButtons();
    }

    private void setButtons(){
        ArrayList<String> dateRanges = new ArrayList<String>();
        for(ArrayList<String> currRange : ranges){
            String dateEdges = currRange.get(0) + " - " + currRange.get(currRange.size() - 1);
            dateRanges.add(dateEdges);
        }
        for(int i = 0; i < dateRanges.size(); i++){
            if(i < 4){
                int option;
                if(i == 0){
                    option = R.id.btnOption1;
                } else if(i == 1){
                    option = R.id.btnOption2;
                } else if(i == 2){
                    option = R.id.btnOption3;
                } else {
                    option = R.id.btnOption4;
                }
                Button btnCurr = findViewById(option);
                btnCurr.setVisibility(View.VISIBLE);
                btnCurr.setText(dateRanges.get(i));
            }

        }
    }

    private void createSeparateArrayLists(){
        ArrayList<String> range = new ArrayList<>();
        for(int i = 0; i < finalMeetTimes.size(); i++){
            if(finalMeetTimes.get(i).equals("rangeBreak")){
                ranges.add(range);
                range = new ArrayList<>();
                finalMeetTimes.remove(i);
                i--;
            } else {
                range.add(finalMeetTimes.get(i));
            }
        }
        ranges.add(range);
        Log.d("rangeCheck", String.valueOf(ranges.size()));
    }


    private ArrayList<String> removedNotEnoughTime() throws ParseException {
        ArrayList<String> withTimesRemoved = new ArrayList<>();
        for (int i = 0; i < finalMeetTimes.size(); i++) {
            if (finalMeetTimes.get(i).equals("break")) {
                finalMeetTimes.remove(i);
                i--;
                if(!withTimesRemoved.get(withTimesRemoved.size() - 1).equals("rangeBreak")){
                    withTimesRemoved.add("rangeBreak");
                }
            } else if (finalMeetTimes.indexOf("break") < 0 || finalMeetTimes.indexOf("break") - i + 1 >= duration) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = sdf.parse(finalMeetTimes.get(i));
                withTimesRemoved.add(new java.text.SimpleDateFormat("EEEE, HH:mm").format(date));
            }
        }
        return withTimesRemoved;
    }

}


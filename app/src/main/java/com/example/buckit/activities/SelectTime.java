package com.example.buckit.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckit.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SelectTime extends AppCompatActivity {

    ArrayList<String> finalMeetTimes;
    ArrayList<ArrayList<String>> ranges;
    ArrayList<String> toDisplay;
    private HashMap<String, Integer> userEvents;
    JSONArray sentTimes;
    ListView lvMeetTimes;
    ArrayAdapter<String> meetTimesAdapter;
    int duration;
    Button btnOption1, btnOption2, btnOption3, btnOption4, btnBookIt;
    ArrayList<Button> selectTimeButtons;
    TextView tvSelectedTime;
    Integer position;
    HashMap<String, String> stringToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        ranges = new ArrayList<>();
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        toDisplay = new ArrayList<>();
        stringToDate = new HashMap<>();
        selectTimeButtons = new ArrayList<>();
        String meetTimes = getIntent().getStringExtra("InviteTimes");
        position = getIntent().getIntExtra("position", 0);
        duration = getIntent().getIntExtra("duration", 0);
        defineButtons();
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
        lvMeetTimes = findViewById(R.id.lvMeetTimes);
        lvMeetTimes.setAdapter(meetTimesAdapter);
        setListTimeListeners();
    }

    private void defineButtons(){
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnBookIt = findViewById(R.id.btnBookIt);
        selectTimeButtons.add(btnOption1);
        selectTimeButtons.add(btnOption2);
        selectTimeButtons.add(btnOption3);
        selectTimeButtons.add(btnOption4);
        selectTimeButtons.add(btnBookIt);
    }

    private void setListTimeListeners(){
        lvMeetTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String time = toDisplay.get(position);
                tvSelectedTime.setText(time);
            }
        });

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
        toDisplay.add("");
        meetTimesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toDisplay);
        setButtons();
        setButtonListeners();
    }

    private void setButtonListeners(){
        for (Button btn : selectTimeButtons) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == btnBookIt.getId()){
                        if(tvSelectedTime.getText().toString().length() > 0){
                            try{
                                addToCalendar();
                            } catch(ParseException e){
                                e.printStackTrace();
                            }
                            finalizeActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please select a time", Toast.LENGTH_SHORT).show();
                        }
                    }else if(v.getId() == btnOption1.getId()){
                        toDisplay.clear();
                        toDisplay.addAll(ranges.get(0));
                        v.setSelected(true);
                        setUnselected(btnOption2, btnOption3, btnOption4);
                    } else if(v.getId() == btnOption2.getId()){
                        toDisplay.clear();
                        toDisplay.addAll(ranges.get(1));
                        v.setSelected(true);
                        setUnselected(btnOption1, btnOption3, btnOption4);
                    } else if(v.getId() == btnOption3.getId()){
                        toDisplay.clear();
                        toDisplay.addAll(ranges.get(2));
                        v.setSelected(true);
                        setUnselected(btnOption1, btnOption2, btnOption4);
                    } else if(v.getId() == btnOption4.getId()){
                        toDisplay.clear();
                        toDisplay.addAll(ranges.get(3));
                        v.setSelected(true);
                        setUnselected(btnOption1, btnOption2, btnOption3);
                    }
                    meetTimesAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void addToCalendar() throws java.text.ParseException {
        Calendar beginTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = sdf.parse(stringToDate.get(tvSelectedTime.getText().toString()));
        beginTime.setTime(date);
        long calID = 4;
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, date.getTime());
        values.put(CalendarContract.Events.DTEND, date.getTime()+3600000);
        values.put(CalendarContract.Events.TITLE, getIntent().getStringExtra("title"));
        values.put(CalendarContract.Events.EVENT_LOCATION, getIntent().getStringExtra("location"));
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }



    private void finalizeActivity(){
        if(tvSelectedTime.equals(R.string.select_time)){
            Toast.makeText(this, "Please select a time!", Toast.LENGTH_SHORT).show();
        } else{
            Intent i = new Intent();
            i.putExtra("position", position);
            i.putExtra("final time", stringToDate.get(tvSelectedTime.getText().toString()));
            setResult(RESULT_OK, i);
            finish();
        }
    }

    private void setUnselected(Button btn1, Button btn2, Button btn3){
        btn1.setSelected(false);
        btn2.setSelected(false);
        btn3.setSelected(false);
    }

    private void setButtons(){
        ArrayList<String> dateRanges = new ArrayList<String>();
        for(ArrayList<String> currRange : ranges){
            String dateEdges = currRange.get(0) + " - " + currRange.get(currRange.size() - 1);
            dateRanges.add(dateEdges);
        }
        for(int i = 0; i < dateRanges.size(); i++){
            if(i < 4){
                Button btnCurr;
                if(i == 0){
                    btnCurr = btnOption1;
                } else if(i == 1){
                    btnCurr = btnOption2;
                } else if(i == 2){
                    btnCurr = btnOption3;
                } else {
                    btnCurr = btnOption4;
                }
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
                if(withTimesRemoved.size() > 1){
                    if(!withTimesRemoved.get(withTimesRemoved.size() - 1).equals("rangeBreak")){
                        withTimesRemoved.add("rangeBreak");
                    }
                }
            } else if (finalMeetTimes.indexOf("break") < 0 || finalMeetTimes.indexOf("break") - i + 1 >= duration) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = sdf.parse(finalMeetTimes.get(i));
                String possibleDate = new java.text.SimpleDateFormat("EEEE, HH:mm").format(date);
                withTimesRemoved.add(possibleDate);
                stringToDate.put(possibleDate, finalMeetTimes.get(i));
            }
        }
        return withTimesRemoved;
    }

}


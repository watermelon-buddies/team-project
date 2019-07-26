package com.example.buckit.fragments;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.buckit.R;
import com.example.buckit.models.User;
import com.example.buckit.models.UserInvite;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@TargetApi(26)
public class SchedulerFragment extends Fragment {

    private Unbinder unbinder;
    private ArrayList<Button> schedulerButtons;
    private ArrayList<String> meetTimes;
    private Button btnSend;
    private HashMap<String, Integer> userEvents;
    private ArrayList<User> recentUsers = new ArrayList<>();
    private ParseUser userToInvite;
    @BindView(R.id.tvEventTitle) TextView tvEventTitle;
    @BindView(R.id.etLocation) EditText etLocation;
    @BindView (R.id.btnRecent0) Button btnRecent0;
    @BindView (R.id.btnRecent1) Button btnRecent1;
    @BindView (R.id.btnRecent2) Button btnRecent2;
    @BindView (R.id.btnRecent3) Button btnRecent3;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View schedulerView = inflater.inflate(R.layout.activity_scheduler_fragment, container, false);
        unbinder = ButterKnife.bind(this, schedulerView);
        meetTimes = new ArrayList<>();
        userEvents = new HashMap<>();
        if (getArguments() != null){
            userEvents = (HashMap<String, Integer>) getArguments().getSerializable("userEvents");
        }
        return schedulerView;
    }

    @Override
    public void onViewCreated(@NonNull View schedulerView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(schedulerView, savedInstanceState);
        createButtonArray(schedulerView);
        btnSend = schedulerView.findViewById(R.id.btnSend);
        schedulerButtons.add(btnSend);
        getRecentUsers();
        addListeners();
    }

    private void getRecentUsers(){
        User.Query userQuery = new User.Query();
        userQuery.getTop();
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        Log.d("Find Users", objects.get(i).getUsername());
                        recentUsers.add(objects.get(i));
                    }
                    setButtons();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addUserInvite(){
        /* TODO: make this an array of users or make only one selectable*/
        if(btnRecent0.isSelected()){
            userToInvite = recentUsers.get(0);
        }
    }

    private void setButtons(){
        String initialsUser0 = recentUsers.get(0).getUsername().substring(0,2);
        String initialsUser1 = recentUsers.get(1).getUsername().substring(0,2);
        String initialsUser2 = recentUsers.get(2).getUsername().substring(0,2);
        String initialsUser3 = recentUsers.get(3).getUsername().substring(0,2);
        btnRecent0.setText(initialsUser0);
        btnRecent1.setText(initialsUser1);
        btnRecent2.setText(initialsUser2);
        btnRecent3.setText(initialsUser3);
    }

    private void constructCalendarList() {
        for(Button btn : schedulerButtons){
            if(btn.isSelected()) {
                String btnText = (String) btn.getText();
                if(btnText.equals("Mon")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.MONDAY)));}
                if(btnText.equals("Tue")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.TUESDAY)));}
                if(btnText.equals("Wed")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.WEDNESDAY))); }
                if(btnText.equals("Thur")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.THURSDAY))); }
                if(btnText.equals("Fri")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.FRIDAY))); }
                if(btnText.equals("Sat")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.SATURDAY))); }
                if(btnText.equals("Sun")){ addDayToArray(new java.text.SimpleDateFormat("dd/MM/yyyy").format(getNextWeekday(DayOfWeek.SUNDAY))); }
                Log.d("bigcheck", String.valueOf(meetTimes.size()));
                /*TODO: Bug control*/
            }
        }
    }

    private void removeBusyTimes(){
        Log.d("beforeRemove", String.valueOf(meetTimes.size()));
        for(int i = 0; i < meetTimes.size(); i++){
            String currTime = meetTimes.get(i);
            if(userEvents.containsKey(currTime)){
                for(int j = 0; j < userEvents.get(currTime) - 1; j++){
                    if(i < meetTimes.size()){
                        meetTimes.remove(i);
                    }
                }
                if(i == meetTimes.size()){
                    meetTimes.add("break");
                } else {
                    meetTimes.set(i, "break");
                }

            }
        }
        for(String time : meetTimes){
            Log.d("check", time);
        }
        Log.d("after remove", String.valueOf(meetTimes.size()));
    }

    private void addDayToArray(String day){
        addTimes(day);
        Log.d("check", String.valueOf(meetTimes.size()));
    }

    private void addTimes(String day){
        for(Button btn : schedulerButtons){
            if(btn.isSelected()){
                String btnText = (String) btn.getText();
                if(btnText.equals("Morning")){ addSpecificTimes(7, 10, day); }
                if(btnText.equals("Afternoon")){ addSpecificTimes(11, 15, day); }
                if(btnText.equals("Evening")) { addSpecificTimes(16, 19, day); }
                if(btnText.equals("Night")) { addSpecificTimes(20, 23, day); }
            }
        }
    }

    private void addSpecificTimes(int start, int end, String day){
        for(int i = start; i <= end; i++){
            for(int j = 0; j <= 45; j+=15){
                String newTime = day;
                String hour = i < 10 ? "0" + String.valueOf(i) : String.valueOf(i);
                String minute = j == 0 ? "00" : String.valueOf(j);
                newTime += " " + hour + ":" + minute + ":00";
                meetTimes.add(newTime);
            }
        }
    }



    private void addListeners() {
        for (Button btn : schedulerButtons) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == btnSend.getId()){
                        constructCalendarList();
                        removeBusyTimes();
                        sendInvite();
                    }
                    if (v.isSelected()) {
                        v.setSelected(false);
                    } else {
                        v.setSelected(true);
                    }
                }
            });
        }
    }

    private void sendInvite(){
        addUserInvite();
        UserInvite newInvite = new UserInvite();
        newInvite.setTitle(tvEventTitle.getText().toString());
        newInvite.setLocation(etLocation.getText().toString());
        newInvite.setCreator(ParseUser.getCurrentUser());
        newInvite.setMeetTimes(meetTimes);
        newInvite.setInvited(userToInvite);
        newInvite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("InviteCheck", "Post created successfully");
                } else{
                    Log.d("InviteCheck", "Post error");
                }
            }
        });


    }

    @TargetApi(26)
        public Date getNextWeekday(DayOfWeek day){
        Calendar cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        int today = cal.get(Calendar.DAY_OF_WEEK);
        int daysToAdd = (day.getValue() - today + 7) % 7;
        cal.add(Calendar.DAY_OF_YEAR, daysToAdd + 1);
        return cal.getTime();
    }

    private void createButtonArray(View schedulerView) {
        schedulerButtons = new ArrayList<>();
        Button btnMonday = schedulerView.findViewById(R.id.btnMonday);
        Button btnTuesday = schedulerView.findViewById(R.id.btnTuesday);
        Button btnWednesday = schedulerView.findViewById(R.id.btnWednesday);
        Button btnThursday = schedulerView.findViewById(R.id.btnThursday);
        Button btnFriday = schedulerView.findViewById(R.id.btnFriday);
        Button btnSaturday = schedulerView.findViewById(R.id.btnSaturday);
        Button btnSunday = schedulerView.findViewById(R.id.btnSunday);
        Button btnMorning = schedulerView.findViewById(R.id.btnMorning);
        Button btnAfternoon = schedulerView.findViewById(R.id.btnAfternoon);
        Button btnEvening = schedulerView.findViewById(R.id.btnEvening);
        Button btnNight = schedulerView.findViewById(R.id.btnNight);
        schedulerButtons.add(btnRecent0);
        schedulerButtons.add(btnRecent1);
        schedulerButtons.add(btnRecent2);
        schedulerButtons.add(btnRecent3);
        schedulerButtons.add(btnMonday);
        schedulerButtons.add(btnTuesday);
        schedulerButtons.add(btnWednesday);
        schedulerButtons.add(btnThursday);
        schedulerButtons.add(btnFriday);
        schedulerButtons.add(btnSaturday);
        schedulerButtons.add(btnSunday);
        schedulerButtons.add(btnMorning);
        schedulerButtons.add(btnAfternoon);
        schedulerButtons.add(btnEvening);
        schedulerButtons.add(btnNight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

package com.example.buckit.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckit.R;
import com.example.buckit.activities.ViewFriends;
import com.example.buckit.activities.ViewProfile;
import com.example.buckit.models.NotificationSender;
import com.example.buckit.models.User;
import com.example.buckit.models.UserInvite;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.buckit.fragments.EventsExploreFragment.KEY_SELECTED_CATEGORIES;

@TargetApi(26)
public class SchedulerFragment extends Fragment {


    public static int SELECT_FRIEND_CODE = 100;
    private String userSelected;
    private Unbinder unbinder;
    private ArrayList<Button> schedulerButtons;
    private ArrayList<String> meetTimes;
    private ArrayList<String> userFriendsList;
    private Button btnSend;
    private HashMap<String, Integer> userEvents;
    private HashMap<String, User> userFriends;
    private ArrayList<Button> recentFriendButtons = new ArrayList<>();
    private ParseUser userToInvite;
    private View schedulerView;
    private HashMap<String, ImageView> checkmarks;

    private int durationIn15MinIntervals;
    @BindView(R.id.tvRecents) TextView tvRecents;
    @BindView(R.id.spinnerHours) Spinner spinnerHours;
    @BindView(R.id.etEventName) EditText etEventName;
    @BindView(R.id.tvAddInvitees) TextView addInvitees;
    @BindView(R.id.spinnerMinutes) Spinner spinnerMinutes;
    @BindView(R.id.tvEventTitle) TextView tvEventTitle;
    @BindView(R.id.etLocation) EditText etLocation;
    @BindView (R.id.btnRecent0) Button btnRecent0;
    @BindView (R.id.btnRecent1) Button btnRecent1;
    @BindView (R.id.btnRecent2) Button btnRecent2;
    @BindView (R.id.btnRecent3) Button btnRecent3;
    @BindView (R.id.fabViewFriends) FloatingActionButton fabViewFriends;
    @BindView(R.id.ivCheckmark0) ImageView ivCheckmark0;
    @BindView(R.id.ivCheckmark1) ImageView ivCheckmark1;
    @BindView(R.id.ivCheckmark2) ImageView ivCheckmark2;
    @BindView(R.id.ivCheckmark3) ImageView ivCheckmark3;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        schedulerView = inflater.inflate(R.layout.activity_scheduler_fragment, container, false);
        unbinder = ButterKnife.bind(this, schedulerView);
        meetTimes = new ArrayList<>();
        userEvents = new HashMap<>();
        userFriends = new HashMap<>();
        userFriendsList = new ArrayList<>();
        checkmarks = new HashMap<>();
        buildCheckmarkMap();
        populateFriends();
        addFriendsSetUp();
        if (getArguments() != null){
            userEvents = (HashMap<String, Integer>) getArguments().getSerializable("userEvents");
            userSelected = getArguments().getString("userSelected");
        }
        if(userSelected!=null){
            addInvitees.setText(userSelected);
        }
        return schedulerView;
    }

    private void addFriendsSetUp(){
        fabViewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewFriends = new Intent(getActivity(), ViewFriends.class);
                viewFriends.putExtra("scheduler", true);
                startActivityForResult(viewFriends, SELECT_FRIEND_CODE);
            }
        });
    }

    private void buildCheckmarkMap(){
        checkmarks.put("0", ivCheckmark0);
        checkmarks.put("1", ivCheckmark1);
        checkmarks.put("2", ivCheckmark2);
        checkmarks.put("3", ivCheckmark3);
    }

    public void populateFriends() {
        User.Query userQuery = new User.Query();
        userQuery.getTop().withFriends();
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> object, ParseException e) {
                if (e == null) {
                    if (object != null && object.size() > 0){
                        ParseObject user = object.get(0);  // only one match expected
                        // now get the user's friends
                        List<User> friends = user.getList("friends");
                        for(User friend : friends){
                            userFriends.put(friend.getUsername(), friend);
                            userFriendsList.add(friend.getUsername());
                        }
                        Log.d("Timeline Activity", "Successfully loaded friends!");
                    }
                    setButtons();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View schedulerView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(schedulerView, savedInstanceState);
        checkCategories();
        createButtonArray(schedulerView);
        btnSend = schedulerView.findViewById(R.id.btnSend);
        schedulerButtons.add(btnSend);
        userSelected = getArguments().getString("userSelected");
        addListeners();
    }

    private void checkCategories(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL(currentUser);
        acl.setPublicReadAccess(true);
        currentUser.setACL(acl);
        JSONArray categories = currentUser.getJSONArray(KEY_SELECTED_CATEGORIES);
        if (categories == null){
            ArrayList<String> catEmptyList = new ArrayList<>();
            currentUser.put(KEY_SELECTED_CATEGORIES, catEmptyList);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("Home Activity", "Create a new post success!");
                    } else {
                        Log.d("Home Activity", "Failed in creating a post!");
                        e.printStackTrace();
                    }
                }
            });
        }
    }



    private void addUserInvite(){
        userToInvite = userFriends.get(addInvitees.getText().toString());
    }

    private void setButtons(){
        if(userFriendsList.size() > 0) {
            tvRecents.setVisibility(View.VISIBLE);
            Button btnToSend = btnRecent0;
            for (int i = 0; i < userFriendsList.size(); i++) {
                if (i == 1) {
                    btnToSend = btnRecent1;
                } else if (i == 2) {
                    btnToSend = btnRecent2;
                } else if (i == 3) {
                    btnToSend = btnRecent3;
                }
                handleSetUp(userFriendsList.get(i), btnToSend);
            }
        }
    }


    private void handleSetUp(String original, Button btn){
        String initials = original.substring(0,1) + original.substring((original.indexOf(" ")+1), (original.indexOf(" ") + 2));
        btn.setVisibility(View.VISIBLE);
        btn.setText(initials);
        recentFriendButtons.add(btn);
    }

    @SuppressLint("SimpleDateFormat")
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
                /*TODO: Bug control*/
            }
        }
    }




    private void removeBusyTimes(){
        Log.d("beforeRemove", String.valueOf(meetTimes.size()));
        for(int i = 1; i < meetTimes.size(); i++){
            String currTime = meetTimes.get(i);
            if(userEvents.containsKey(currTime)) {
                meetTimes.remove(i);
                if (!meetTimes.get(i - 1).equals("break")) {
                    if (i == meetTimes.size()) {
                        meetTimes.add("break");
                    } else {
                        meetTimes.set(i, "break");
                    }

                }
            }
        }

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
                    if(v.getId() == btnSend.getId()) {
                        constructCalendarList();
                        if (meetTimes.size() == 0 || addInvitees.getText().toString().equals("")|| etLocation.getText().toString().equals("")| etEventName.getText().toString().equals("")) {
                            Toast.makeText(getContext(), R.string.warning, Toast.LENGTH_SHORT).show();
                        } else {
                            removeBusyTimes();
                            sendInvite();
                            startActivity(new Intent(getActivity(), ViewProfile.class));
                            sendNotification();
                            getActivity().finish();
                        }
                    }
                    if (v.isSelected()) {
                        if(v.getId() == btnRecent0.getId() || v.getId() == btnRecent1.getId() || v.getId() == btnRecent2.getId() || v.getId() == btnRecent3.getId()){
                            checkmarks.get(v.getTag()).setVisibility(View.INVISIBLE);
                            addInvitees.setText("");
                        }
                        v.setSelected(false);

                    } else {
                        v.setSelected(true);
                        if(v.getId() == btnRecent0.getId() || v.getId() == btnRecent1.getId() || v.getId() == btnRecent2.getId() || v.getId() == btnRecent3.getId()){
                            checkmarks.get(v.getTag()).setVisibility(View.VISIBLE);
                            unselectOtherRecents(v);
                        }

                    }
                }
            });
        }
    }

    private void sendNotification(){
        NotificationSender makeNotification = new NotificationSender(((User)userToInvite).getUsername(), ((User)userToInvite).getDeviceId(), 0, ParseUser.getCurrentUser().getUsername());
        makeNotification.sendNotification();
    }


    private void unselectOtherRecents(View button){
        for(int i = 0; i < recentFriendButtons.size(); i++){
            if(button.getId() == recentFriendButtons.get(i).getId()){
                addInvitees.setText(userFriendsList.get(i));
            } else {
                recentFriendButtons.get(i).setSelected(false);
                checkmarks.get(recentFriendButtons.get(i).getTag()).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void calculateDuration(){
        String initialHours = spinnerHours.getSelectedItem().toString();
        String finalHours = initialHours.substring(0,calculateEnd(initialHours));
        String initialMinutes = spinnerMinutes.getSelectedItem().toString();
        String finalMinutes = initialMinutes.substring(0,calculateEnd(initialMinutes));
        durationIn15MinIntervals = (Integer.valueOf(finalHours) * 4) + (Integer.valueOf(finalMinutes) / 15);
    }

    private int calculateEnd(String time){
        if(time.charAt(1) == ' '){
            return 1;
        } else {
            return 2;
        }
    }

    private void sendInvite(){
        addUserInvite();
        calculateDuration();
        UserInvite newInvite = new UserInvite();
        newInvite.setTitle(etEventName.getText().toString());
        newInvite.setLocation(etLocation.getText().toString());
        newInvite.setCreator(ParseUser.getCurrentUser());
        newInvite.setMeetTimes(meetTimes);
        newInvite.setInvited(userToInvite);
        newInvite.setDuration(durationIn15MinIntervals);
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

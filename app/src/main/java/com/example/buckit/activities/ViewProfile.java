package com.example.buckit.activities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buckit.LoginActivity;
import com.example.buckit.R;
import com.example.buckit.adapters.PendingInvitesAdapter;
import com.example.buckit.models.NotificationSender;
import com.example.buckit.models.User;
import com.example.buckit.models.UserInvite;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.buckit.adapters.PendingInvitesAdapter.SELECT_TIME_REQUEST_CODE;

public class ViewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Unbinder unbinder;
    ArrayList<UserInvite> mPendingInvites;
    ArrayList<UserInvite> mComingUpInvites;
    PendingInvitesAdapter mPendingInvitesAdapter;
    PendingInvitesAdapter mComingUpInvitesAdapter;
    @BindView(R.id.rvPendingInvites) RecyclerView rvPendingInvites;
    @BindView(R.id.rvComingUp) RecyclerView rvComingUp;
    @BindView(R.id.tvPending) TextView tvPending;
    @BindView(R.id.drawer_layout)
    DrawerLayout leftDrawer;
    @BindView(R.id.nav_view)
    NavigationView leftDrawerNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    HashMap<String, Integer> userCal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_drawrer_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.view_profile);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        userCal = (HashMap<String, Integer>) getIntent().getSerializableExtra("userCal");
        mPendingInvites = new ArrayList<UserInvite>();
        mComingUpInvites = new ArrayList<>();
        mPendingInvitesAdapter = new PendingInvitesAdapter(mPendingInvites, userCal, this, true);
        mComingUpInvitesAdapter = new PendingInvitesAdapter(mComingUpInvites, userCal, this, false);
        rvPendingInvites.setAdapter(mPendingInvitesAdapter);
        rvComingUp.setAdapter(mComingUpInvitesAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPendingInvites.setLayoutManager(linearLayoutManager);
        rvComingUp.setLayoutManager(linearLayoutManager2);
        populatePendingInvites();
        populateComingUpInvites(true);
        customView();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, leftDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        leftDrawer.addDrawerListener(toggle);
        toggle.syncState();
        leftDrawerNavigationView.setNavigationItemSelectedListener(this);
    }


    protected void populatePendingInvites() {
        final UserInvite.Query userInviteQuery = new UserInvite.Query();
        userInviteQuery.getTop().withInvited().withAccepted();
        userInviteQuery.whereEqualTo(UserInvite.KEY_INVITED, ParseUser.getCurrentUser());
        userInviteQuery.whereEqualTo(UserInvite.KEY_ACCEPTED, null);
        userInviteQuery.findInBackground(new FindCallback<UserInvite>() {
            @Override
            public void done(List<UserInvite> object, ParseException e) {
                if (e == null) {
                    mPendingInvites.clear();
                    mPendingInvites.addAll(object);
                    mPendingInvitesAdapter.notifyDataSetChanged();
                    Log.d("Timeline Activity", "Successfully loaded posts!");
                    if(mPendingInvites.size() == 0){
                        rvPendingInvites.setVisibility(View.GONE);
                        tvPending.setVisibility(View.GONE);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void populateComingUpInvites(final boolean invited){
        final UserInvite.Query comingUpInviteQuery = new UserInvite.Query();
        comingUpInviteQuery.getTop().withAccepted().withInvited();
        if(invited){
            comingUpInviteQuery.whereEqualTo(UserInvite.KEY_INVITED, ParseUser.getCurrentUser());
        } else {
            comingUpInviteQuery.whereEqualTo(UserInvite.KEY_CREATOR, ParseUser.getCurrentUser());
        }
        comingUpInviteQuery.whereEqualTo(UserInvite.KEY_ACCEPTED, true);
        comingUpInviteQuery.findInBackground(new FindCallback<UserInvite>() {
            @Override
            public void done(List<UserInvite> objects, ParseException e) {
                if(e == null){
                    if(invited){
                        mComingUpInvites.clear();
                    }
                    mComingUpInvites.addAll(objects);
                    mComingUpInvitesAdapter.notifyDataSetChanged();
                    if(invited){
                        populateComingUpInvites(false);
                    }
                    Log.d("Invites", "Successfully loaded events coming up!");
                    addCalendarEvents();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addCalendarEvents(){
        for(UserInvite currInvite : mComingUpInvites){
            if(!currInvite.getAddedToCal()) {
                try {
                    addToCalendar(currInvite.getFinalTime(), currInvite.getDuration(), currInvite.getTitle());
                    currInvite.setAddedToCal();
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void addToCalendar(String finalTime, int duration, String title) throws java.text.ParseException {
        Calendar beginTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = sdf.parse(finalTime);
        beginTime.setTime(date);
        long calID = 4;
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, date.getTime());
        values.put(CalendarContract.Events.DTEND, date.getTime()+3600000);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_LOCATION, getIntent().getStringExtra("location"));
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* TODO Change the navigation items and select to which activity they lead to */

        if (id == R.id.nav_logout) {
            ParseUser.logOut();
            Intent logoutIntent = new Intent(ViewProfile.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
        }
        else if (id == R.id.nav_view_friends) {
            Intent friendsView = new Intent(ViewProfile.this, ViewFriends.class);
            friendsView.putExtra("userCal", userCal);
            startActivity(friendsView);
            finish();
        }
        leftDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SELECT_TIME_REQUEST_CODE){
            final Integer invitePosition = data.getExtras().getInt("position", 0);
            final UserInvite curr = mPendingInvites.get(invitePosition);
            notifyInviter(curr);
            String finalTime = data.getStringExtra("final time");
            curr.setFinalTime(finalTime);
            curr.setAddedToCal();
            curr.setAccepted(true);
            curr.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Invite Booked", Toast.LENGTH_SHORT).show();
                        Log.d("Accept invite", "Accept invite successful");
                        mPendingInvitesAdapter.removeData(invitePosition);
                        mComingUpInvites.add(curr);
                        mComingUpInvitesAdapter.notifyItemInserted(mComingUpInvites.size()-1);
                    } else {
                        Log.d("Accept invite", "Failed in booking invite");
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void notifyInviter(UserInvite curr){
        NotificationSender makeNotification = new NotificationSender(((User)curr.getCreator()).getDeviceId(), 1, curr.getInvited().getUsername());
        makeNotification.sendNotification();
    }

    @SuppressLint("ResourceAsColor")
    private void customView(){
        View headerLayout = leftDrawerNavigationView.getHeaderView(0);
        headerLayout.setBackgroundColor(R.color.bright_blue);
        User user = (User) ParseUser.getCurrentUser();
        TextView tvUsername = headerLayout.findViewById(R.id.tvUsername);
        ImageView ivUserProfilePic = headerLayout.findViewById(R.id.ivUserProfilePic);
        tvUsername.setText(user.getUsername());
        if (user.getProfilePic() != null){
            Glide.with(this)
                    .load(user.getProfilePic().getUrl())
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivUserProfilePic);
        }
        else{
            Glide.with(this)
                    .load(R.drawable.no_profile)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivUserProfilePic);
        }
    }
}

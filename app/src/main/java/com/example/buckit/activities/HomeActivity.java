package com.example.buckit.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.example.buckit.fragments.BucketListTabbed;
import com.example.buckit.fragments.EventsExploreFragment;
import com.example.buckit.fragments.SchedulerFragment;
import com.example.buckit.models.User;
import com.example.buckit.utils.ExploreActivityPermissionDispatcher;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout leftDrawer;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.nav_view) NavigationView leftDrawerNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private final static String KEY_LOCATION = "location";
    public static final String KEY_PROFILE_PICTURE= "profilePic";
    private final static long UPDATE_INTERVAL = 1000000;  /* 1000 secs */
    private final static long FASTEST_INTERVAL = 500000; /* 500 secs */
    private final static long EPOCH_MILLI_MONTH = 62L * 24L * 60L * 60L * 1000L;
    public static final long ONE_MINUTE_IN_MILLIS=60000;
    public final static String LAT_KEY = "lat";
    public final static String LONG_KEY = "long";
    final private static int CALENDAR_CALLBACK_ID = 42;
    private LocationRequest mLocationRequest;
    public HashMap<String, Integer> userEvents;
    public Location mCurrentLocation;
    private ParseUser currentUser;
    private String userSchedulerSelected;
    private FragmentManager fragmentManager;
    private Fragment fragment;

    /* HomeActivity after sucessfully logging in that contains BucketListFragment,
    EventsExploreFragment and SchedulerFragment */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_drawrer_main);
        Log.d("device id", FirebaseInstanceId.getInstance().getToken());
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.home_activity_content);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        currentUser = ParseUser.getCurrentUser();
        getCalendarEvents(CALENDAR_CALLBACK_ID, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        setSupportActionBar(toolbar);
        setUpFragments();
        customView();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, leftDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        leftDrawer.addDrawerListener(toggle);
        toggle.syncState();
        leftDrawerNavigationView.setNavigationItemSelectedListener(this);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
    }


    @SuppressLint("ResourceAsColor")
    private void customView(){
        View headerLayout = leftDrawerNavigationView.getHeaderView(0);
        headerLayout.setBackgroundColor(R.color.bright_blue);
        User user = (User) currentUser;
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




    public void setUpFragments(){
        fragmentManager = getSupportFragmentManager();
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                  new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_bucket:
                                fragment = new BucketListTabbed();
                                break;
                            case R.id.action_schedule:
                                Bundle userCal = new Bundle();
                                userCal.putSerializable("userEvents", userEvents);
                                userCal.putString("userSelected", "");
                                fragment = new SchedulerFragment();
                                fragment.setArguments(userCal);
                                break;
                            case R.id.action_events:
                                Bundle bundle = new Bundle();
                                bundle.putDouble(LAT_KEY, mCurrentLocation.getLatitude());
                                bundle.putDouble(LONG_KEY, mCurrentLocation.getLongitude());
                                fragment = new EventsExploreFragment();
                                fragment.setArguments(bundle);
                                break;
                            default:
                                fragment = new BucketListTabbed();
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flmain,
                                fragment).commit();
                        return true;
                    }
                });
        bottomNavigationView.setSelectedItemId(R.id.action_bucket);
    }

    private void createSchedulerFragment(){
        Bundle userCal = new Bundle();
        userCal.putSerializable("userEvents", userEvents);
        userCal.putString("userSelected", userSchedulerSelected);
        fragment = new SchedulerFragment();
        fragment.setArguments(userCal);
        fragmentManager.beginTransaction().replace(R.id.flmain,
                fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data.getExtras() != null){
                userSchedulerSelected = data.getExtras().getString("selected_user");
                createSchedulerFragment();
            }
        }
    }



    @Override
    public void onPause() {
        super.onPause();
    }


        @Override
    public void onBackPressed() {
        if (leftDrawer.isDrawerOpen(GravityCompat.START)) {
            ImageView ivUserProfilePic = findViewById(R.id.ivUserProfilePic);
            TextView name = findViewById(R.id.tvUsername);
            name.setText(currentUser.getUsername());
            Glide.with(this)
                    .load(currentUser.getParseFile(KEY_PROFILE_PICTURE).getUrl())
                    .into(ivUserProfilePic);
            leftDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /* TODO Check for need of other option on task bar*/



    @SuppressWarnings("StatementWithEmptyBody")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* TODO Change the navigation items and select to which activity they lead to */

        if (id == R.id.nav_logout) {
            ParseUser.logOut();
            Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
       }
        else if (id == R.id.nav_view_friends) {
            Intent friendsView = new Intent(HomeActivity.this, ViewFriends.class);
            friendsView.putExtra("userCal", userEvents);
            startActivity(friendsView);
       } else if (id == R.id.nav_profile) {
            //getCalendarEvents(CALENDAR_CALLBACK_ID, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
            Intent profileView = new Intent(HomeActivity.this, ViewProfile.class);
            profileView.putExtra("userCal", userEvents);
            startActivity(profileView);

        }
        leftDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCalendarEvents(int callbackId, String... permissionsId) {
        userEvents = new HashMap<String, Integer>();
        long today = new Date().getTime();
        long nextMonth = today + EPOCH_MILLI_MONTH;
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }
        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
        else {
            CalendarProvider provider = new CalendarProvider(getApplicationContext());
            List<me.everything.providers.android.calendar.Calendar> calendars = provider.getCalendars().getList();
            for (Calendar currCal : calendars) {
                if (!currCal.name.equals("Holidays in United States") && !currCal.name.equals("Contacts")) {
                    for (me.everything.providers.android.calendar.Event currEvent : provider.getEvents(currCal.id).getList()) {
                        // Checks events are happening within the range of two months
                        if ((currEvent.dTStart) >= today && (currEvent.dTStart) <= nextMonth) {
                            Long timeOfEvent = ((currEvent.dTend - currEvent.dTStart) / 1000) / 60;
                            Integer rangeIn15MinIntervals = Math.toIntExact(timeOfEvent) / 15;
                            for(int i = 0; i < rangeIn15MinIntervals; i++){
                                String normalDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(currEvent.dTStart + (15 * i * ONE_MINUTE_IN_MILLIS)));
                                userEvents.put(normalDate, 1);
                            }
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ExploreActivityPermissionDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getMyLocation() {

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExploreActivityPermissionDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION})
    public void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        mCurrentLocation = location;
        Log.d("Location", mCurrentLocation.toString());
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

}

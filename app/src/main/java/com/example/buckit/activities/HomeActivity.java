package com.example.buckit.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
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

import com.example.buckit.LoginActivity;
import com.example.buckit.R;
import com.example.buckit.fragments.BucketListTabbed;
import com.example.buckit.fragments.EventsExploreFragment;
import com.example.buckit.fragments.SchedulerFragment;
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
import com.parse.ParseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
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

    private LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    public HashMap<String, Integer> userEvents;
    private long UPDATE_INTERVAL = 1000000;  /* 1000 secs */
    private long FASTEST_INTERVAL = 500000; /* 500 secs */
    private final static long EPOCH_MILLI_MONTH = 62L * 24L * 60L * 60L * 1000L;
    public final static String LAT_KEY = "lat";
    public final static String LONG_KEY = "long";
    final private static int calendarCallbackId = 42;


    /* HomeActivity after sucessfully logging in that contains BucketListFragment,
    EventsExploreFragment and SchedulerFragment
    *
    * */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        getCalendarEvents(calendarCallbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, leftDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        leftDrawer.addDrawerListener(toggle);
        toggle.syncState();
        leftDrawerNavigationView.setNavigationItemSelectedListener(this);

        final FragmentManager fragmentManager = getSupportFragmentManager();
            // handle navigation selection
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            Fragment fragment;
                            switch (item.getItemId()) {
                                case R.id.action_bucket:
                                    fragment = new BucketListTabbed();
                                    break;
                                case R.id.action_schedule:
                                    Bundle userCal = new Bundle();
                                    userCal.putSerializable("userEvents", userEvents);
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


        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_bucket);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

    }


    @Override
    public void onBackPressed() {
        if (leftDrawer.isDrawerOpen(GravityCompat.START)) {
            leftDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /* TODO Check for need of other option on task bar*/

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
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
        //else if (id == R.id.nav_view_friends) {
//
//        } else if (id == R.id.nav_tools) {
//
//        } else if (id == R.id.nav_logout) {
//
//        }
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
                            String normalDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(currEvent.dTStart));
                            Log.d("check", normalDate + " " + String.valueOf(rangeIn15MinIntervals));
                            userEvents.put(normalDate, rangeIn15MinIntervals);
                        }
                    }
                }
            }
            Log.d("Check", String.valueOf(userEvents.size()));
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

        // Report to the UI that the location was updated

        mCurrentLocation = location;
        Log.d("Location", mCurrentLocation.toString());
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }
}

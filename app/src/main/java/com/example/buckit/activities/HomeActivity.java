package com.example.buckit.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.buckit.EventExplore;
import com.example.buckit.R;
import com.example.buckit.fragments.BucketListFragment;
import com.example.buckit.fragments.EventsExploreFragment;
import com.example.buckit.fragments.SchedulerFragment;
import com.example.buckit.utils.ExploreActivityPermissionDispatcher;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;


    private final static String KEY_LOCATION = "location";

    private LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    public ArrayList<ArrayList> userEvents;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000;
    private final static long EPOCH_MILLI_MONTH = 62L * 24L * 60L * 60L * 1000L;
    public final static String LAT_KEY = "lat";
    public final static String LONG_KEY = "long";
    final private static int calendarCallbackId = 42;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


//     Define a request code to send to Google Play services This code is
//     returned in Activity.onActivityResult

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getCalendarEvents(calendarCallbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_bucket:
                                fragment = new BucketListFragment();
                                break;
                            case R.id.action_schedule:
                                fragment = new SchedulerFragment();
                                break;
                            case R.id.action_events:
                                Bundle bundle = new Bundle();
                                bundle.putDouble(LAT_KEY, mCurrentLocation.getLatitude());
                                bundle.putDouble(LONG_KEY, mCurrentLocation.getLongitude());
                                fragment = new EventsExploreFragment();
                                fragment.setArguments(bundle);
                                break;
                            default:
                                fragment = new SchedulerFragment();
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flmain, fragment).commit();
                        return true;
                    }
                });


        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_schedule);

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCalendarEvents(int callbackId, String... permissionsId) {
        userEvents = new ArrayList<>();
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
            List<Calendar> calendars = provider.getCalendars().getList();
            for (Calendar currCal : calendars) {
                if (!currCal.name.equals("Holidays in United States") && !currCal.name.equals("Contacts")) {
                    for (me.everything.providers.android.calendar.Event currEvent : provider.getEvents(currCal.id).getList()) {
                        // Checks events are happening within the range of two months
                        if ((currEvent.dTStart) >= today && (currEvent.dTStart) <= nextMonth) {
                            ArrayList<Long> currEventTimeRange = new ArrayList<>();
                            currEventTimeRange.add(currEvent.dTStart);
                            currEventTimeRange.add(currEvent.dTend);
                            userEvents.add(currEventTimeRange);
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

        // Display the connection status

        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude());
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!",
                    Toast.LENGTH_SHORT).show();
        }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
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
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }
}

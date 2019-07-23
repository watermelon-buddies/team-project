package com.example.buckit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckit.adapters.SwipeCardAdapter;
import com.example.buckit.models.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class EventExplore extends AppCompatActivity implements CardStack.CardEventListener {


    public final static String API_BASE_URL = "https://www.eventbriteapi.com/v3/events/search";
    public final static String API_KEY_PARAM = "token";
    public final static String privateToken = "NTGHZITV2KGOOD67X3DX";
    public final static String API_KEY_LOCATION = "location.address";
    public final static String API_KEY_LATITUDE = "location.latitude";
    public final static String API_KEY_LONGITUDE = "location.longitude";
    private final static String KEY_LOCATION = "location";

    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    ImageView blur;


    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public CardStack rvEvents;
    public SwipeCardAdapter swipe_card_adapter;
    HashMap<Integer, Event> eventsList;
    BottomNavigationView bottomNavigationView;
    public TextView tvEventTitle;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            getEvents();
        }
        eventsList = new HashMap<>();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setContentView(R.layout.activity_main);
        rvEvents = findViewById(R.id.rvEvents);
        tvEventTitle = findViewById(R.id.tvEventTitle);
        rvEvents.setContentResource(R.layout.event_item_view);
        rvEvents.setListener(this);
        swipe_card_adapter = new SwipeCardAdapter(getApplicationContext(), 20, eventsList);
        rvEvents.setAdapter(swipe_card_adapter);
        final int callbackId = 42;
        checkPermissions(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
    }

    private void getEvents() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(API_KEY_LOCATION, "newyork");
        params.put(API_KEY_LATITUDE, mCurrentLocation.getLatitude());
        params.put(API_KEY_LONGITUDE, mCurrentLocation.getLongitude());
        params.put(API_KEY_PARAM, privateToken);
        client.get(API_BASE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray events = response.getJSONArray("events");
                    for (int i = 0; i < events.length(); i++) {
                        Event currEvent = new Event(events.getJSONObject(i));
                        eventsList.put(i, currEvent);
                        swipe_card_adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.d("Get events", "Failure to retrieve events");
                }
            }
        });
    }


    @Override
    public boolean swipeEnd(int section, float distance) {
        return distance > 200;
    }

    @Override
    public boolean swipeStart(int section, float distance) {
        return true;
    }

    @Override
    public boolean swipeContinue(int section, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void discarded(int mIndex, int direction) {
    }

    @Override
    public void topCardTapped() {
        onButtonShowPopupWindowClick(findViewById(R.id.rvEvents));
    }

    public void onButtonShowPopupWindowClick(View view) {
        addBlur();
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.events_explore_popup_window, null);
        final TextView tvSaveEvent = popupView.findViewById(R.id.tvSaveEvent);
        ImageView ivClose = popupView.findViewById(R.id.ivClose);
        ivClose.bringToFront();
        tvSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "Save Event");
            }
        });
        final TextView tvBuckit = popupView.findViewById(R.id.tvBuckit);
        tvBuckit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "Buck event");
            }
        });
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                removeBlur();
            }
        });
    }

    private void addBlur() {
        blur = findViewById(R.id.addItemFab);
        blur.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        blur.startAnimation(aniFade);
    }

    private void removeBlur() {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        blur.startAnimation(aniFade);
        blur.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkPermissions(int callbackId, String... permissionsId) {
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
                    List<me.everything.providers.android.calendar.Event> events = provider.getEvents(currCal.id).getList();
                    for (me.everything.providers.android.calendar.Event currEvent : events) {
                        if (currEvent.dTStart >= Instant.now().toEpochMilli()) {
                            // make an arraylist of arraylists
                            Log.d("check", currEvent.title);

                        }
                    }
                }
            }
            List<me.everything.providers.android.calendar.Event> events = provider.getEvents(4).getList();
            String date = String.valueOf(provider.getEvent(62).dTStart);
        }
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
/*      ExploreActivityPermissionDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);*/
        if (requestCode == 42 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            CalendarProvider provider = new CalendarProvider(getApplicationContext());
            List<Calendar> calendars = provider.getCalendars().getList();
        }
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


    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
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
        getEvents();
        String msg = "Updated Location: " +
                location.getLatitude() + "," +
                location.getLongitude();
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

}

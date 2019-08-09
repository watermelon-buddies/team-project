package com.example.buckit.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buckit.LoginActivity;
import com.example.buckit.R;
import com.example.buckit.fragments.BucketListTabbed;
import com.example.buckit.fragments.EventsExploreFragment;
import com.example.buckit.fragments.SchedulerFragment;
import com.example.buckit.models.User;
import com.example.buckit.models.UserInvite;
import com.example.buckit.models.UserNotification;
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
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import permissions.dispatcher.NeedsPermission;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.buckit.models.User.KEY_NOTIFICATIONS;
import static com.example.buckit.models.UserNotification.KEY_IS_SEEN;
import static com.example.buckit.models.UserNotification.KEY_TO_NOTIFY;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout leftDrawer;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.nav_view)
    NavigationView leftDrawerNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private final static String KEY_LOCATION = "location";
    private static final String KEY_FRIEND_NOTIFICATION = "friendNotification";
    private static final String KEY_EVENT_NOTIFICATION = "eventNotification";
    public static final String KEY_PROFILE_PICTURE = "profilePic";
    private final static long UPDATE_INTERVAL = 1000000;  /* 1000 secs */
    private final static long FASTEST_INTERVAL = 500000; /* 500 secs */
    private final static long EPOCH_MILLI_MONTH = 62L * 24L * 60L * 60L * 1000L;
    public static final long ONE_MINUTE_IN_MILLIS = 60000;
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
    private JSONArray notifications;
    private ArrayList<String> newNotifications;
    private ImageView ivNotifications;
    private ImageView ivNotificationCircle;
    private View popupView;
    private PopupWindow popupWindow;
    private HashMap<String, String> notificationToType;

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
        notificationToType = new HashMap<>();
        ButterKnife.bind(this);
        ivNotifications = findViewById(R.id.ivNotifications);
        ivNotificationCircle = findViewById(R.id.ivNotificationCircle);
        newNotifications = new ArrayList<>();
        currentUser = ParseUser.getCurrentUser();
        getCalendarEvents(CALENDAR_CALLBACK_ID, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        setSupportActionBar(toolbar);
        setUpFragments();
        customView();
        prepareNotifications();
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

    private void prepareNotifications() {
        UserNotification.Query getNotifcations = new UserNotification.Query();
        getNotifcations.whereEqualTo(KEY_TO_NOTIFY, currentUser.getUsername());
        getNotifcations.whereEqualTo(KEY_IS_SEEN, false);
        getNotifcations.findInBackground(new FindCallback<UserNotification>() {
            @Override
            public void done(List<UserNotification> object, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < object.size(); i++) {
                        UserNotification current = object.get(i);
                        newNotifications.add(current.getMessage());
                        notificationToType.put(current.getMessage(), current.getType());
                        current.setSeen(true);
                        current.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("NotificationHome", "Succeeded in creating a post!");
                                } else {
                                    Log.d("NotificationHome", "Failed in creating a post!");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (newNotifications.size() > 0) {
                        ivNotificationCircle.setVisibility(View.VISIBLE);
                    } else {
                        newNotifications.add(getResources().getString(R.string.noCurrentNotifications));
                        notificationToType.put(getResources().getString(R.string.noCurrentNotifications), "noType");
                    }
                    notificationIconListener();
                } else {
                    e.printStackTrace();
                }
            }
        });

    }


    @SuppressLint("ResourceAsColor")
    private void customView() {
        View headerLayout = leftDrawerNavigationView.getHeaderView(0);
        headerLayout.setBackgroundColor(R.color.bright_blue);
        User user = (User) currentUser;
        TextView tvUsername = headerLayout.findViewById(R.id.tvUsername);
        ImageView ivUserProfilePic = headerLayout.findViewById(R.id.ivUserProfilePic);
        tvUsername.setText(user.getUsername());
        if (user.getProfilePic() != null) {
            Glide.with(this)
                    .load(user.getProfilePic().getUrl())
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivUserProfilePic);
        } else {
            Glide.with(this)
                    .load(R.drawable.no_profile)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivUserProfilePic);
        }
    }

    private void notificationIconListener() {
        ivNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "See Notifications", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = inflater.inflate(R.layout.notifications_popup, null);
                final ListView lvNotifications = popupView.findViewById(R.id.lvNotifications);
                ArrayAdapter<String> notificationsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, newNotifications) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);
                        tv.setTextColor(Color.GRAY);
                        return view;
                    }
                };
                lvNotifications.setAdapter(notificationsAdapter);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                popupWindow = new PopupWindow(popupView, width, height);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAsDropDown(ivNotifications, -100, -10);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                popupWindow.setElevation(30);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        currentUser.put(KEY_NOTIFICATIONS, newNotifications);
                    }
                });
                setItemListeners(lvNotifications);
            }
        });
    }

    private void setItemListeners(ListView lvNotifications){
        lvNotifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = notificationToType.get(newNotifications.get(position));
                if(type.equals(KEY_EVENT_NOTIFICATION)){
                    Intent profile = new Intent(HomeActivity.this, ViewProfile.class);
                    startActivity(profile);
                } else if(type.equals(KEY_FRIEND_NOTIFICATION)){
                    Intent friends = new Intent(HomeActivity.this, ViewFriends.class);
                    startActivity(friends);
                }
            }
        });
    }


    public void setUpFragments() {
        fragmentManager = getSupportFragmentManager();
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
/*                                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                                Float latitude = sharedPreferences.getFloat("latitude", (float) 37.483149);
                                Float longitude = sharedPreferences.getFloat("longitude", (float) -122.150028);*/
                                if (mCurrentLocation != null){
                                    bundle.putDouble(LAT_KEY, mCurrentLocation.getLatitude());
                                    bundle.putDouble(LONG_KEY, mCurrentLocation.getLongitude());
                                }
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

    private void createSchedulerFragment() {
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
        if (resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
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
        } else if (id == R.id.nav_view_friends) {
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
                if (currCal != null) {
                    if (!currCal.name.equals("Holidays in United States") && !currCal.name.equals("Contacts")) {
                        for (me.everything.providers.android.calendar.Event currEvent : provider.getEvents(currCal.id).getList()) {
                            // Checks events are happening within the range of two months
                            if ((currEvent.dTStart) >= today && (currEvent.dTStart) <= nextMonth) {
                                Long timeOfEvent = ((currEvent.dTend - currEvent.dTStart) / 1000) / 60;
                                Integer rangeIn15MinIntervals = Math.toIntExact(timeOfEvent) / 15;
                                for (int i = 0; i < rangeIn15MinIntervals; i++) {
                                    String normalDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(currEvent.dTStart + (15 * i * ONE_MINUTE_IN_MILLIS)));
                                    userEvents.put(normalDate, 1);
                                }
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
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("latitude", (float) mCurrentLocation.getLatitude());
        editor.putFloat("longitude", (float) mCurrentLocation.getLongitude());
        editor.commit();

    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

}

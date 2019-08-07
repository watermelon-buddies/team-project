package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.buckit.R;
import com.example.buckit.adapters.SwipeCardAdapter;
import com.example.buckit.models.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseUser;
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cdflynn.android.library.checkview.CheckView;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.buckit.activities.HomeActivity.LAT_KEY;
import static com.example.buckit.activities.HomeActivity.LONG_KEY;
import static com.parse.Parse.getApplicationContext;


public class EventsExploreFragment extends Fragment implements CardStack.CardEventListener {

    /* Fragment in which the user is able to explore events happening nearby, customized
    by the interests expressed in their bucketlist.
     */


    public final static String API_BASE_URL = "https://www.eventbriteapi.com/v3/";
    public final static String API_KEY_PARAM = "token";
    public final static String PRIVATE_TOKEN = getApplicationContext().getResources().getString(R.string.token);
    public final static String API_KEY_LATITUDE = "location.latitude";
    public final static String API_KEY_LONGITUDE = "location.longitude";
    public final static String API_KEY_AREA_RADIUS = "location.within";
    public final static String API_KEY_LOCATION = "location.address";
    public final static String API_KEY_CATEGORY = "categories";
    public final static String KEY_SELECTED_CATEGORIES = "catSelected";
    public View popupView;
    public SwipeCardAdapter swipe_card_adapter;
    public Double latitude;
    public Double longitude;
    public HashMap<Integer, Event> eventsList;
    @BindView(R.id.rvEvents) public CardStack rvEvents;
    @BindView(R.id.ivBlur) public ImageView blur;
    @BindView(R.id.tvPosition) TextView tvPosition;
    @BindView(R.id.check)
    CheckView mCheck;
    @BindView(R.id.ivCalSuccess) ImageView ivCalSuccess;
    @BindView(R.id.tvCalSuccess) TextView tvCalSuccess;
    private Unbinder unbinder;
    private int swiped;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View eventExploreView = inflater.inflate(R.layout.events_explore_fragment, container, false);
        unbinder = ButterKnife.bind(this, eventExploreView);
        return eventExploreView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Sets cardstack view and sets adapter. We get user location from home activity to create event
    // request to Eventbrite API
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParseUser user = ParseUser.getCurrentUser();
        eventsList = new HashMap<>();
        swiped = 0;
        rvEvents.setContentResource(R.layout.event_card_adapter);
        tvPosition.setText(String.valueOf(0));
        swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(),20, eventsList, tvPosition, mCheck, ivCalSuccess, tvCalSuccess);
        rvEvents.setAdapter(swipe_card_adapter);
        rvEvents.setListener(this);
        if (getArguments() != null){
            latitude = getArguments().getDouble(LAT_KEY);
            longitude = getArguments().getDouble(LONG_KEY);
            getEvents(user);
        }
        mCheck.bringToFront();

    }

    // Sends request specifying location for events. Result list is used to create event types
    private void getEvents(ParseUser user) {
        ArrayList<String> categories = (ArrayList<String>) user.get(KEY_SELECTED_CATEGORIES);
        if (categories.get(0) == null) {
            Log.d("Categories", "is null");
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            String url = API_BASE_URL + "events/search";
            params.put(API_KEY_AREA_RADIUS, user.getString("eventRadius"));
            params.put(API_KEY_LATITUDE, latitude);
            params.put(API_KEY_LONGITUDE, longitude);
            params.put(API_KEY_PARAM, PRIVATE_TOKEN);
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray events = response.getJSONArray("events");
                        for (int j = 0; j < events.length(); j++) {
                            Event currEvent = new Event(events.getJSONObject(j));
                            eventsList.put(j, currEvent);
                            swipe_card_adapter.notifyDataSetChanged();;
                        }
                    } catch (JSONException e) {
                        Log.d("Get events", "Failure to retrieve events");
                    }
                }
            });

        }
        else {
            final int[] position = {0};
            for (int i = 0;i < categories.size(); i++) {
                Log.d("Categories", "is not null");
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                String url = API_BASE_URL + "events/search";
                params.put(API_KEY_AREA_RADIUS, "20mi");
                params.put(API_KEY_LATITUDE, latitude);
                params.put(API_KEY_LONGITUDE, longitude);
                params.put(API_KEY_PARAM, PRIVATE_TOKEN);
                Log.d("Events", "Loading category "+categories.get(i));
                params.put(API_KEY_CATEGORY, categories.get(i));
                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray events = response.getJSONArray("events");
                            for (int j = 0; j < events.length(); j++) {
                                Event currEvent = new Event(events.getJSONObject(j));
                                eventsList.put(position[0], currEvent);
                                position[0]++;
                                swipe_card_adapter.notifyDataSetChanged();;
                            }
                        } catch (JSONException e) {
                            Log.d("Get events", "Failure to retrieve events");
                        }
                    }
                });
            }
        }

    }

    public void triggerCheckmark(){
        mCheck.check();
        mCheck.bringToFront();
    }

    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    // Makes sure swipe only discards card if user swiped it enough to prevent accidental swipes
    @Override
    public boolean swipeEnd(int section, float distance) {
        if(distance>200){
            int newPosition = Integer.parseInt(tvPosition.getText().toString());
            newPosition++;
            tvPosition.setText(String.valueOf(newPosition));
            Log.d("Booking", tvPosition.getText().toString());
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean swipeStart(int section, float distance) {
        Log.d("check", eventsList.get(0).getTitle());
        return true;
    }

    @Override
    public boolean swipeContinue(int section, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void discarded(int mIndex, int direction) {

    }

    // If user taps on event, popup window is created revealing options to save event for
    // later or put it in their calendar
    @Override
    public void topCardTapped() {

    }




    public void onButtonShowPopupWindowClick(View view) {
        addBlur();
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.events_explore_popup_window, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);
        addBlur();
        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                removeBlur();
            }
        });
        TextView tvSaveEvent = popupView.findViewById(R.id.tvSaveEvent);
        ImageView ivClose = popupView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                removeBlur();
            }
        });
        TextView tvBuckit = popupView.findViewById(R.id.tvBuckit);
        setListeners(tvSaveEvent, ivClose, tvBuckit);
    }

    private void setListeners(TextView tvSaveEvent, ImageView ivClose, TextView tvBuckit) {
        ivClose.bringToFront();
        tvSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "Save Event");
            }
        });
        tvBuckit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "Buck event");
            }
        });


    }

    // Blur options in the back and disable
    private void addBlur() {
        blur.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        blur.startAnimation(aniFade);
    }

    private void removeBlur(){
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        blur.startAnimation(aniFade);
        blur.setVisibility(View.INVISIBLE);
    }

}
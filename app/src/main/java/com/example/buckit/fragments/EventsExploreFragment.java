package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cdflynn.android.library.checkview.CheckView;
import cz.msebera.android.httpclient.Header;

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
    public final static String KEY_DEFAULT_LOCATION = "Menlo Park";
    public final static String KEY_DEFAULT_RADIUS = "100mi";
    public View popupView;
    public SwipeCardAdapter swipe_card_adapter;
    public float latitude;
    public float longitude;
    public HashMap<Integer, Event> eventsList;
    @BindView(R.id.rvEvents) public CardStack rvEvents;
    @BindView(R.id.ivBlur) public ImageView blur;
    @BindView(R.id.tvPosition) TextView tvPosition;
    @BindView(R.id.check)
    CheckView mCheck;
    @BindView(R.id.ivCalSuccess) ImageView ivCalSuccess;
    @BindView(R.id.tvCalSuccess) TextView tvCalSuccess;
    ProgressBar progressBar;
    private Unbinder unbinder;
    private int responseLength;

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
        rvEvents.setContentResource(R.layout.event_card_adapter);
        tvPosition.setText(String.valueOf(0));
        progressBar = getActivity().findViewById(R.id.actionProgressBar);
        swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(),20, eventsList, tvPosition, mCheck, ivCalSuccess, tvCalSuccess);
        rvEvents.setAdapter(swipe_card_adapter);
        rvEvents.setListener(this);
        if (getArguments() != null){
            latitude = getArguments().getFloat(LAT_KEY);
            longitude = getArguments().getFloat(LONG_KEY);
            getDummyEvents();
        }
        mCheck.bringToFront();

    }

    // Sends request specifying location for events. Result list is used to create event types
    private void getEvents(ParseUser user) {
        ArrayList<String> categories = (ArrayList<String>) user.get(KEY_SELECTED_CATEGORIES);
        //final int[] position = {4};
            showProgressBar();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            String url = API_BASE_URL + "events/search";
            params.put(API_KEY_AREA_RADIUS, KEY_DEFAULT_RADIUS);
/*            if (latitude != null){
                params.put(API_KEY_LATITUDE, latitude);
                params.put(API_KEY_LONGITUDE, longitude);
            }*/
            params.put(API_KEY_LOCATION, KEY_DEFAULT_LOCATION);
            params.put(API_KEY_PARAM, PRIVATE_TOKEN);
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Log.d("Response", response.toString());
                        JSONArray events = response.getJSONArray("events");
                        Log.d("Response", events.toString());
                        responseLength = events.length();
                        for (int j = 4; j < responseLength; j++) {
                            Event currEvent = new Event(events.getJSONObject(j));
                            eventsList.put(j, currEvent);
                            swipe_card_adapter.notifyDataSetChanged();
                            hideProgressBar();
                        }
                    } catch (JSONException e) {
                        Log.d("Get events", "Failure to retrieve events");
                    }
                }
            });

//        else {
//            for (int i = 0;i < categories.size(); i++) {
//                Log.d("Categories", "is not null");
//                showProgressBar();
//                AsyncHttpClient client = new AsyncHttpClient();
//                RequestParams params = new RequestParams();
//                String url = API_BASE_URL + "events/search";
//                params.put(API_KEY_AREA_RADIUS, KEY_DEFAULT_RADIUS);
//                params.put(API_KEY_LOCATION, KEY_DEFAULT_LOCATION);
//                params.put(API_KEY_PARAM, PRIVATE_TOKEN);
//                Log.d("Events", "Loading category "+categories.get(i));
//                params.put(API_KEY_CATEGORY, categories.get(i));
//                client.get(url, params, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            position[0]++;
//                            JSONArray events = response.getJSONArray("events");
//                            responseLength = events.length();
//                            for (int j = 0; j < responseLength; j++) {
//                                Event currEvent = new Event(events.getJSONObject(j));
//                                eventsList.put(position[0], currEvent);
//                                position[0]++;
//                                swipe_card_adapter.notifyDataSetChanged();
//                                hideProgressBar();
//                            }
//                        } catch (JSONException e) {
//                            Log.d("Get events", "Failure to retrieve events");
//                        }
//                    }
//                });
//            }
//        }
    }

    private void getDummyEvents(){
        eventsList.clear();
        ArrayList<String> urls = new ArrayList<>();
        String url0 = API_BASE_URL + "events/61520805385";
        urls.add(url0);
        String url1 = API_BASE_URL + "events/26807011493";
        urls.add(url1);
        String url2 = API_BASE_URL + "events/53078098020";
        urls.add(url2);
        String url3 = API_BASE_URL + "events/55845412131";
        urls.add(url3);
        for(int i = 3; i >= 0; i--){
            addDummyEvent(urls.get(i), i);
        }
    }

    private void addDummyEvent(String url, final int i){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, PRIVATE_TOKEN);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Event currEvent = new Event(response);
                    eventsList.put(i, currEvent);
                    if(i == 0){
                        getEvents(ParseUser.getCurrentUser());
                    }
                } catch (JSONException e) {
                    Log.d("Get events", "Failure to retrieve events");
                    e.printStackTrace();
                }
            }
        });
    }


    // Makes sure swipe only discards card if user swiped it enough to prevent accidental swipes
    @Override
    public boolean swipeEnd(int section, float distance) {
        if(distance>200){
            int newPosition = Integer.parseInt(tvPosition.getText().toString());
            newPosition++;
            tvPosition.setText(String.valueOf(newPosition));
            Log.d("Booking", tvPosition.getText().toString());
            Log.d("Booking", String.valueOf(responseLength));
            if ((newPosition % 25) == 0) {
                swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(),20, eventsList, tvPosition, mCheck, ivCalSuccess, tvCalSuccess);
                rvEvents.setAdapter(swipe_card_adapter);
                rvEvents.setListener(this);
                Toast.makeText(getContext(), "Out of events! Reloading new ones!", Toast.LENGTH_SHORT).show();
                getDummyEvents();
            }
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


    public void showProgressBar() {
        // Show progress item
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        progressBar.setVisibility(View.GONE);
    }

}
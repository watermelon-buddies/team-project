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
    @BindView(R.id.check) CheckView mCheck;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParseUser user = ParseUser.getCurrentUser();
        eventsList = new HashMap<>();
        rvEvents.setContentResource(R.layout.event_card_adapter);
        tvPosition.setText(String.valueOf(0));
        progressBar = getActivity().findViewById(R.id.actionProgressBar);
        swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(), 20, eventsList, tvPosition, mCheck, ivCalSuccess, tvCalSuccess);
        rvEvents.setAdapter(swipe_card_adapter);
        rvEvents.setListener(this);
        if (getArguments() != null) {
            latitude = getArguments().getFloat(LAT_KEY);
            longitude = getArguments().getFloat(LONG_KEY);
            getEvents(user);
        }
        mCheck.bringToFront();

    }

    private void getEvents(ParseUser user) {
        ArrayList<String> categories = (ArrayList<String>) user.get(KEY_SELECTED_CATEGORIES);
        if (categories == null || categories.size() == 0) {
            showProgressBar();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            String url = API_BASE_URL + "events/search";
            params.put(API_KEY_AREA_RADIUS, KEY_DEFAULT_RADIUS);
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
                        for (int j = 0; j < responseLength; j++) {
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

        } else {
            final int[] position = {0};
            for (int i = 0; i < categories.size(); i++) {
                Log.d("Categories", "is not null");
                showProgressBar();
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                String url = API_BASE_URL + "events/search";
                params.put(API_KEY_AREA_RADIUS, KEY_DEFAULT_RADIUS);
                params.put(API_KEY_LOCATION, KEY_DEFAULT_LOCATION);
                params.put(API_KEY_PARAM, PRIVATE_TOKEN);
                Log.d("Events", "Loading category " + categories.get(i));
                params.put(API_KEY_CATEGORY, categories.get(i));
                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray events = response.getJSONArray("events");
                            responseLength = events.length();
                            for (int j = 0; j < responseLength; j++) {
                                Event currEvent = new Event(events.getJSONObject(j));
                                eventsList.put(position[0], currEvent);
                                position[0]++;
                                swipe_card_adapter.notifyDataSetChanged();
                                hideProgressBar();
                            }
                        } catch (JSONException e) {
                            Log.d("Get events", "Failure to retrieve events");
                        }
                    }
                });
            }
        }

    }

    @Override
    public boolean swipeEnd(int section, float distance) {
        if (distance > 200) {
            int newPosition = Integer.parseInt(tvPosition.getText().toString());
            newPosition++;
            tvPosition.setText(String.valueOf(newPosition));
            Log.d("Booking", tvPosition.getText().toString());
            Log.d("Booking", String.valueOf(responseLength));
            if ((newPosition % 25) == 0) {
                swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(), 20, eventsList, tvPosition, mCheck, ivCalSuccess, tvCalSuccess);
                rvEvents.setAdapter(swipe_card_adapter);
                rvEvents.setListener(this);
                Toast.makeText(getContext(), "Out of events! Reloading new ones!", Toast.LENGTH_SHORT).show();
                getEvents(ParseUser.getCurrentUser());
            }
            return true;
        } else {
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

    @Override
    public void topCardTapped() {

    }


    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

}
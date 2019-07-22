package com.example.buckit.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buckit.R;

import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.buckit.adapters.SwipeCardAdapter;
import com.example.buckit.models.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.parse.Parse.getApplicationContext;


public class EventsExploreFragment extends Fragment implements CardStack.CardEventListener {

    public final static String API_BASE_URL = "https://www.eventbriteapi.com/v3/events/search";
    public final static String API_KEY_PARAM = "token";
    public final static String privateToken = "NTGHZITV2KGOOD67X3DX";
    public final static String API_KEY_LOCATION = "location.address";

    public final static String API_KEY_LATITUDE = "location.latitude";
    public final static String API_KEY_LONGITUDE = "location.longitude";

    public CardStack rvEvents;
    public ImageView ivBlur;
    public SwipeCardAdapter swipe_card_adapter;
    Location mCurrentLocation;
    Double latitude;
    Double longitude;
    HashMap<Integer, Event> eventsList;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_events_explore_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsList = new HashMap<>();
        rvEvents = (CardStack) view.findViewById(R.id.rvEvents);
        rvEvents.setContentResource(R.layout.item_event);
        ivBlur = view.findViewById(R.id.ivBlur);
        rvEvents.setListener(this);
        swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(),20, eventsList);
        rvEvents.setAdapter(swipe_card_adapter);
        if (getArguments() != null){
            latitude = getArguments().getDouble("lat");
            longitude = getArguments().getDouble("long");
            getEvents();
        }
    }

    private void getEvents() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(API_KEY_LATITUDE, latitude);
        params.put(API_KEY_LONGITUDE, longitude);
        params.put(API_KEY_PARAM, privateToken);
        client.get(API_BASE_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    JSONArray events = response.getJSONArray("events");
                    for(int i = 0; i < events.length(); i++){
                        Event currEvent = new Event(events.getJSONObject(i));
                        eventsList.put(i, currEvent);
                        swipe_card_adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e){
                    Log.d("Get events", "Failure to retrieve events");
                }
            }
        });
    }



    @Override
    public boolean swipeEnd(int section, float distance) {

        return (distance>200)? true : false;

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

        onButtonShowPopupWindowClick(rvEvents);
    }

    public void onButtonShowPopupWindowClick(View view) {
        ImageView blur = ivBlur;
        blur.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        blur.startAnimation(aniFade);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
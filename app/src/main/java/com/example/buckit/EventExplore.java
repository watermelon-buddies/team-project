package com.example.buckit;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

public class EventExplore extends AppCompatActivity implements CardStack.CardEventListener {

    public final static String API_BASE_URL = "https://www.eventbriteapi.com/v3/events/search";
    public final static String API_KEY_PARAM = "token";
    public final static String privateToken = "NTGHZITV2KGOOD67X3DX";
    public final static String API_KEY_LOCATION = "location.address";
    public CardStack rvEvents;
    public SwipeCardAdapter swipe_card_adapter;
    HashMap<Integer, Event> eventsList;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getEvents();
        setSupportActionBar(toolbar);
        eventsList = new HashMap<>();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setContentView(R.layout.activity_main);
        rvEvents = (CardStack) findViewById(R.id.rvEvents);
        rvEvents.setContentResource(R.layout.item_event);
        rvEvents.setListener(this);
        swipe_card_adapter = new SwipeCardAdapter(getApplicationContext(),20, eventsList);
        rvEvents.setAdapter(swipe_card_adapter);
    }

    private void getEvents() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(API_KEY_LOCATION, "sanfrancisco");
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
        return true;
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

    }
}

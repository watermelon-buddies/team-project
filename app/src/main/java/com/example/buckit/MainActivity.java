package com.example.buckit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public final static String API_BASE_URL = "https://www.eventbriteapi.com/v3/events/search";
    public final static String API_KEY_PARAM = "token";
    public final static String privateToken = "NTGHZITV2KGOOD67X3DX";
    public final static String API_KEY_LOCATION = "location.address";

    AsyncHttpClient client;
    ArrayList<String> events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new AsyncHttpClient();
        getEvents();
    }

    private void getEvents() {
        RequestParams params = new RequestParams();
        params.put(API_KEY_LOCATION, "sanfrancisco");
        params.put(API_KEY_PARAM, privateToken);
        client.get(API_BASE_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    JSONArray events = response.getJSONArray("events");
                    JSONObject firstEvent = events.getJSONObject(0);
                    Log.d("Sanity Check", firstEvent.getJSONObject("name").getString("text"));
                    super.onSuccess(statusCode, headers, response);
                } catch (JSONException e){

                }

            }
        });

    }
}

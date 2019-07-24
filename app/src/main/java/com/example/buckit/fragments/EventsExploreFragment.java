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
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.buckit.activities.HomeActivity.LAT_KEY;
import static com.example.buckit.activities.HomeActivity.LONG_KEY;
import static com.parse.Parse.getApplicationContext;


public class EventsExploreFragment extends Fragment implements CardStack.CardEventListener {

    /* Fragment in which the user is able to explore events happening nearby, customized
    by the interests expressed in their bucketlist.
     */


    public final static String API_BASE_URL = "https://www.eventbriteapi.com/v3/events/search";
    public final static String API_KEY_PARAM = "token";
    public final static String PRIVATE_TOKEN = getApplicationContext().getResources().getString(R.string.token);
    public final static String API_KEY_LATITUDE = "location.latitude";
    public final static String API_KEY_LONGITUDE = "location.longitude";
    public View popupView;
    ImageView ivClose;
    public SwipeCardAdapter swipe_card_adapter;
    public Double latitude;
    public Double longitude;
    public HashMap<Integer, Event> eventsList;
    @BindView(R.id.rvEvents) public CardStack rvEvents;
    @BindView(R.id.ivBlur) public ImageView blur;
    private Unbinder unbinder;




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
        eventsList = new HashMap<>();
        rvEvents.setContentResource(R.layout.event_item_view);
        rvEvents.setListener(this);
        swipe_card_adapter = new SwipeCardAdapter(getContext().getApplicationContext(),20, eventsList);
        rvEvents.setAdapter(swipe_card_adapter);
        if (getArguments() != null){
            latitude = getArguments().getDouble(LAT_KEY);
            longitude = getArguments().getDouble(LONG_KEY);
            getEvents();
        }
    }

    // Sends request specifying location for events. Result list is used to create event types
    private void getEvents() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(API_KEY_LATITUDE, latitude);
        params.put(API_KEY_LONGITUDE, longitude);
        params.put(API_KEY_PARAM, PRIVATE_TOKEN);
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



    // Makes sure swipe only discards card if user swiped it enough to prevent accidental swipes
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

    // If user taps on event, popup window is created revealing options to save event for
    // later or put it in their calendar
    @Override
    public void topCardTapped() {
        onButtonShowPopupWindowClick(rvEvents);
    }


    public void onButtonShowPopupWindowClick(View view) {
        addBlur();
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.events_explore_popup_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
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
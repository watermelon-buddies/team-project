package com.example.buckit.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.Event;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cdflynn.android.library.checkview.CheckView;

public class SwipeCardAdapter extends ArrayAdapter<HashMap<Integer, Event>>  {

    /* Called in order to fill in information in each card of event explorer
     */

    Context mContext;
    HashMap<Integer, Event> mEvents;
    TextView tvAddToCal;
    TextView tvEventTitle;
    TextView tvStartTime;
    TextView tvEventDate;
    String currStartTime;
    String currEndTime;
    String originalStartTime;
    TextView mTvPosition;
    CheckView mCheck;
    ImageView mIvCalSuccess;
    TextView mTvCalSuccess;


    // Events are contained in a hashmap in which the key is their index.
    public SwipeCardAdapter(Context context, int resource, HashMap<Integer, Event> events, TextView tvPosition, CheckView check, ImageView ivCalSuccess, TextView tvCalSuccess) {
        super(context, resource);
        mContext = context;
        mEvents = events;
        mTvPosition = tvPosition;
        mCheck = check;
        mIvCalSuccess = ivCalSuccess;
        mTvCalSuccess = tvCalSuccess;


    }


    // Sets content by getting information from event in hashmap
    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        ImageView ivEventPicture = contentView.findViewById(R.id.ivEventPicture);
        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        final EasyFlipView myEasyFlipView  = contentView.findViewById(R.id.myEasyFlipView);
        myEasyFlipView.setFlipOnTouch(false);
        tvStartTime  = contentView.findViewById(R.id.tvStartTime);
        TextView tvEventDescription = contentView.findViewById(R.id.tvEventDescription);
        tvEventTitle =  contentView.findViewById(R.id.tvDetailTitle);
        ImageView ivEventDetailPicture  = contentView.findViewById(R.id.ivEventDetailPicture);
        ImageView ivGreyBackground = contentView.findViewById(R.id.ivGreyBackground);
        tvEventDate = contentView.findViewById(R.id.tvEventDate);
        ImageView btnFlip = contentView.findViewById(R.id.btnFlip);
        ImageView btnBuckEvent = contentView.findViewById(R.id.btnBuckEvent);
        ImageView btnFlipDetail = contentView.findViewById(R.id.btnFlipDetail);
        btnFlipDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEasyFlipView.flipTheView();
            }
        });
        btnFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEasyFlipView.flipTheView();
            }
        });
        Event event = mEvents.get(position);
        tvTitle.setText(event.getTitle());
        Glide.with(mContext)
                .load(event.getImageUrl())
                .centerCrop()
                .fitCenter()
                .placeholder(R.drawable.grey_placeholder)
                .into(ivEventPicture);
            tvEventDate.setText(event.getDate());
            currStartTime = event.getStartTime();
            currEndTime = event.getEndTime();
            tvStartTime.setText(currStartTime + " - " + currEndTime);
            String original = event.getDescription();
            String cleanString = original.replaceAll("\n", " ");
            tvEventDescription.setText(cleanString);
            tvEventTitle.setText(event.getTitle());
            tvAddToCal = contentView.findViewById(R.id.tvAddToCal);
            setListener(myEasyFlipView);
        return contentView;
    }


    void setListener(final EasyFlipView myEasyFlipView){
        tvAddToCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(mTvPosition.getText().toString());
                Event currEvent = mEvents.get(position);
                Log.d("Booking", currEvent.getTitle());
                try {
                    addToCalendar(currEvent.getOriginalStartTime().replaceAll("T", " "), currEvent.getOriginalEndTime().replaceAll("T", " "), currEvent.getTitle());
                    Toast.makeText(mContext, R.string.added_to_cal, Toast.LENGTH_SHORT).show();
                    mCheck.setVisibility(View.VISIBLE);
                    mCheck.check();
                    mIvCalSuccess.setVisibility(View.VISIBLE);
                    Animation aniFade = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
                    mIvCalSuccess.startAnimation(aniFade);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            removeElements();
                            myEasyFlipView.flipTheView();
                        }
                    }, 2000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void removeElements(){
        Animation out = AnimationUtils.loadAnimation(mContext,R.anim.fade_out);
        mIvCalSuccess.startAnimation(out);
        mCheck.startAnimation(out);
        mIvCalSuccess.setVisibility(View.INVISIBLE);
        mCheck.setVisibility(View.INVISIBLE);


    }

    private void addToCalendar(String startTime, String endTime, String title) throws java.text.ParseException {
        Calendar beginTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = sdf.parse(startTime);
        Date end = sdf.parse(endTime);
        long calID = 4;
        ContentResolver cr = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start.getTime());
        values.put(CalendarContract.Events.DTEND, end.getTime());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }


    @Override
    public int getCount() {
        return mEvents.size();
    }

}


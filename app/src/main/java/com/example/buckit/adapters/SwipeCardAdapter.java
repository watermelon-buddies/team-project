package com.example.buckit.adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.Event;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SwipeCardAdapter extends ArrayAdapter<HashMap<Event, Integer>>  {

    /* Called in order to fill in information in each card of event explorer
     */

    Context mContext;
    HashMap<Integer, Event> mEvents;


    // Events are contained in a hashmap in which the key is their index.
    public SwipeCardAdapter(Context context, int resource, HashMap<Integer, Event> events) {
        super(context, resource);
        mContext = context;
        mEvents = events;

    }


    // Sets content by getting information from event in hashmap
    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        ImageView ivEventPicture = contentView.findViewById(R.id.ivEventPicture);
        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        final EasyFlipView myEasyFlipView  = contentView.findViewById(R.id.myEasyFlipView);
        TextView tvStartTime  = contentView.findViewById(R.id.tvStartTime);
        TextView tvEndingTime  = contentView.findViewById(R.id.tvEndingTime);
        TextView tvEventDescription = contentView.findViewById(R.id.tvEventDescription);
        TextView tvEventTitle =  contentView.findViewById(R.id.tvDetailTitle);

        TextView tvEventDate = contentView.findViewById(R.id.tvEventDate);
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
        final Event event = mEvents.get(position);
        tvTitle.setText(event.getTitle());
        Glide.with(mContext)
                .load(event.getImageUrl())
                .centerCrop()
                .fitCenter()
                .placeholder(R.drawable.grey_placeholder)
                .into(ivEventPicture);
            tvEventDate.setText(event.getDate());
            tvStartTime.setText("Starts @ "+event.getStartTime());
            tvEndingTime.setText("Ends @ "+event.getEndTime());
            tvEventDescription.setText(event.getDescription());
            tvEventTitle.setText(event.getTitle());
        return contentView;
    }



    @Override
    public int getCount() {
        return mEvents.size();
    }

}


package com.example.buckit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.models.Event;

import java.util.HashMap;

public class SwipeCardAdapter extends ArrayAdapter<HashMap<Event, Integer>> {


    float alphaValue = 0;
    private Context mContext;
    ImageView ivEventPicture;
    TextView mtvEventTitle;
    HashMap<Integer, Event> mEvents;



    public SwipeCardAdapter(Context context, int resource, HashMap<Integer, Event> events) {
        super(context, resource);
        mContext = context;
        mEvents = events;


    }


    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        Event event = mEvents.get(position);
        ivEventPicture = contentView.findViewById(R.id.ivEventPicture);
        TextView tvTitlte = contentView.findViewById(R.id.tvTitle);
        tvTitlte.setText(event.getTitle());
        Glide.with(mContext)
                .load(event.getImageUrl())
                .fitCenter()
                .placeholder(R.drawable.grey_placeholder)
                .into(ivEventPicture);
        return contentView;
    }


    @Override
    public int getCount() {
        return mEvents.size();
    }

}


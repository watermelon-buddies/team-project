package com.example.buckit.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.Event;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SwipeCardAdapter extends ArrayAdapter<HashMap<Event, Integer>> {

    /* Called in order to fill in information in each card of event explorer
     */


    Context mContext;
    HashMap<Integer, Event> mEvents;
    @BindView(R.id.ivEventPicture) ImageView ivEventPicture;
    @BindView(R.id.tvTitle) TextView tvTitle;
    private Unbinder unbinder;


    // Events are contained in a hashmap in which the key is their index.
    public SwipeCardAdapter(Context context, int resource, HashMap<Integer, Event> events) {
        super(context, resource);
        mContext = context;
        mEvents = events;

    }


    // Sets content by getting information from event in hashmap
    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        unbinder = ButterKnife.bind(this, contentView);
        tvTitle.setText(mEvents.get(position).getTitle());
        Glide.with(mContext)
                .load(mEvents.get(position).getImageUrl())
                .centerCrop()
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


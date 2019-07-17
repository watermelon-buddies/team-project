package com.example.buckit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.buckit.models.Event;

import java.util.ArrayList;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ViewHolder> {

    private Activity mContext;
    private ArrayList<Event> mEvents;

    public EventCardAdapter(Activity context, ArrayList<Event> events) {
        mContext = context;
        mEvents = events;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event, viewGroup, false);
        return new ViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Event currEvent = mEvents.get(i);
        viewHolder.tvTitle.setText(currEvent.getTitle());
        Glide.with(mContext)
                .load(currEvent.getImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        viewHolder.ivEventPicture.setImageBitmap(resource);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivEventPicture;
        final TextView tvTitle;

        public ViewHolder(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivEventPicture = (ImageView) itemView.findViewById(R.id.ivEventPicture);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

    }
}

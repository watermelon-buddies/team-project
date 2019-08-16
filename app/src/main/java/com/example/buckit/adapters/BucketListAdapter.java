package com.example.buckit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.activities.HomeActivity;
import com.example.buckit.fragments.SchedulerFragment;
import com.example.buckit.models.Bucketlist;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.buckit.models.Bucketlist.KEY_ACHIEVED;

public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder>  {

    TextView btnAchieveItem;
    TextView btnDeleteItem;
    TextView btnBookItem;
    View rootView;
    Bucketlist currentItem;
    private List<Bucketlist> mBucketList;
    private Context mContext;
    public Boolean show;
    public View popupView;
    PopupWindow popupWindow;
    Bucketlist bucketList;
    ViewHolder viewHolder;
    ImageView ivDelete;
    HashMap<String, Integer> mUserEvents;

    public BucketListAdapter(Context context, List<Bucketlist> items, boolean notAchieved, HashMap<String, Integer> userEvents) {
        mBucketList = items;
        mContext = context;
        show = notAchieved;
        mUserEvents = userEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.bucket_list_item_view, parent, false);
        viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        bucketList = mBucketList.get(position);
        holder.btnDone.setTag(bucketList);
        holder.btnScheduler.setTag(bucketList);
        holder.ivDelete.setTag(bucketList);
        holder.tvBucketDescription.setText(bucketList.getName());
        holder.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bucketlist item = mBucketList.get(position);
                if (item != null) {
                    Log.d("Click", "Working!");
                    Bucketlist.Query bucketQuery = new Bucketlist.Query();
                    bucketQuery.whereEqualTo("objectId", item.getObjectId());
                    bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
                        @Override
                        public void done(List<Bucketlist> objects, ParseException e) {
                            Bucketlist selectedItem = objects.get(0);
                            selectedItem.put(KEY_ACHIEVED, true);
                            selectedItem.saveInBackground();
                        }
                    });
                    mBucketList.remove(item);
                    notifyDataSetChanged();
                }

            }
        });
        holder.btnScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                final Bucketlist item = mBucketList.get(position);
                data.putString("activity", item.getName());
                data.putSerializable("userEvents", mUserEvents);
                SchedulerFragment scheduler = new SchedulerFragment();
                scheduler.setArguments(data);
                FragmentTransaction fragmentTransaction = ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flmain, scheduler);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bucketlist item = mBucketList.get(position);
                Log.d("Click", "Working!");
                Bucketlist.Query bucketQuery = new Bucketlist.Query();
                bucketQuery.whereEqualTo("objectId", item.getObjectId());
                bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
                    @Override
                    public void done(List<Bucketlist> objects, ParseException e) {
                        Bucketlist selectedItem = objects.get(0);
                        selectedItem.deleteInBackground();
                    }
                });
                mBucketList.remove(item);
                notifyDataSetChanged();

            }
        });

        int id = mContext.getResources().getIdentifier("com.example.buckit:drawable/cat_" + bucketList.getCategory().toLowerCase().substring(0, 3), null, null);
        Log.d("category", String.valueOf(id));
        Glide.with(mContext)
                .load(id)
                .fitCenter()
                .bitmapTransform(new RoundedCornersTransformation(mContext, 20, 0))
                .into(holder.ivCategoryImage);
    }


    @Override
    public int getItemCount() {
        return mBucketList.size();
    }

    public void clear() {
        mBucketList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Bucketlist> list) {
        mBucketList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBucketDescription;
        public ImageView ivCategoryImage;
        public FloatingActionButton btnDone;
        public FloatingActionButton btnScheduler;
        public FloatingActionButton ivDelete;

        @SuppressLint("RestrictedApi")
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvBucketDescription = itemView.findViewById(R.id.tvBucketDescription);
            btnDone = itemView.findViewById(R.id.btnDone);
            btnScheduler = itemView.findViewById(R.id.btnScheduler);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            if (!show) {
                btnDone.setVisibility(View.INVISIBLE);
                btnScheduler.setVisibility(View.INVISIBLE);
                ivDelete.setVisibility(View.INVISIBLE);
            } else {
                btnDone.setVisibility(View.VISIBLE);
                btnScheduler.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            }

        }
    }


}




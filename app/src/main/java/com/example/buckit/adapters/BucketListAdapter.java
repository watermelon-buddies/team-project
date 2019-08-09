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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.activities.HomeActivity;
import com.example.buckit.fragments.SchedulerFragment;
import com.example.buckit.models.Bucketlist;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.buckit.models.Bucketlist.KEY_ACHIEVED;

public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> implements View.OnClickListener {

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

    public BucketListAdapter(Context context, List<Bucketlist> items, boolean notAchieved) {
        mBucketList = items;
        mContext = context;
        show = notAchieved;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position

        bucketList = mBucketList.get(position);
        holder.btnShowOptionsForItem.setTag(bucketList);
        holder.tvBucketDescription.setText(bucketList.getName());
        int id = mContext.getResources().getIdentifier("com.example.buckit:drawable/cat_" + bucketList.getCategory().toLowerCase().substring(0, 4), null, null);
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
    // for each row, inflate the layout and cache references intop view

    public void clear() {
        mBucketList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Bucketlist> list) {
        mBucketList.addAll(list);
        notifyDataSetChanged();
    }

    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvBucketDescription;
        public ImageView ivCategoryImage;
        public FloatingActionButton btnShowOptionsForItem;

        @SuppressLint("RestrictedApi")
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvBucketDescription = itemView.findViewById(R.id.tvBucketDescription);
            btnShowOptionsForItem = itemView.findViewById(R.id.btnShowOptionsForItem);
            btnShowOptionsForItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentItem = (Bucketlist) btnShowOptionsForItem.getTag();
                    displayPopupWindow(v);
                }
            });
            if (show) btnShowOptionsForItem.setVisibility(View.VISIBLE);
            else btnShowOptionsForItem.setVisibility(View.INVISIBLE);
        }
    }

    private void displayPopupWindow(View anchorView) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.bucketlist_popup, null);
        btnAchieveItem = popupView.findViewById(R.id.btnAchieveItem);
        btnDeleteItem = popupView.findViewById(R.id.btnDeleteItem);
        btnBookItem = popupView.findViewById(R.id.btnBookItem);
        btnBookItem = popupView.findViewById(R.id.btnBookItem);
        btnAchieveItem.setOnClickListener(this);
        btnDeleteItem.setOnClickListener(this);
        btnBookItem.setOnClickListener(this);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchorView, -100, -10);
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.transparent));
    }

    @Override
    public void onClick(View v) {
        Bucketlist item = currentItem;
        if (v.getId() == btnAchieveItem.getId()){
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
                popupWindow.dismiss();
            }
        }
        else if(v.getId() == btnDeleteItem.getId()) {
            if (item != null) {
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
                popupWindow.dismiss();
            }
        }
        else if(v.getId() == btnBookItem.getId()){
            if(item != null){
                Bundle data = new Bundle();
                data.putString("activity", viewHolder.tvBucketDescription.getText().toString());
                SchedulerFragment scheduler = new SchedulerFragment();
                scheduler.setArguments(data);
                FragmentTransaction fragmentTransaction = ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flmain, scheduler);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                popupWindow.dismiss();
            }
        }
    }
}




package com.example.buckit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckit.R;
import com.example.buckit.models.Bucketlist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> {

    private List<Bucketlist> mBucketList;
    private Context mContext;

    public BucketListAdapter(Context context,  List<Bucketlist> items) {
        mBucketList = items;
        mContext = context;
    }
    RecyclerView rvPost;
    private boolean click;
    Bucketlist bucketList;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.bucket_list_item_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position

        bucketList = mBucketList.get(position);
        holder.tvBucketDescription.setText(bucketList.getName());


            /*Bitmap takenImage = null;
            Log.d("Images", "Successfully loaded" + post.getImage().toString());
            try {
                takenImage = BitmapFactory.decodeFile(post.getImage().getFile().getPath());
                System.out.println(takenImage);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.ivImagePost.setImageBitmap(takenImage);*/



        // RESIZE BITMAP, see section below
        // Load the taken image into a preview


        /*holder.ivTweetImage.setVisibility(View.VISIBLE);*/


/*
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 60, 0))
                .into(holder.ivProfileImage);*/

    }

/*    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }*/

    @Override
    public int getItemCount() {
        return mBucketList.size();
    }
    // for each row, inflate the alyout and cache references intop view

    public void clear() {
        mBucketList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Bucketlist> list) {
        mBucketList.addAll(list);
        notifyDataSetChanged();
    }

    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView tvBucketDescription;
            public ImageView ivCategoryImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvBucketDescription = itemView.findViewById(R.id.tvBucketDescription);


        }

        @Override
        public void onClick(View v) {

            }

        }
}

package com.example.buckit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.Bucketlist;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.buckit.models.Bucketlist.KEY_ACHIEVED;
import static com.example.buckit.models.User.KEY_FRIENDS;

public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> {

    private List<Bucketlist> mBucketList;
    private Context mContext;
    private Boolean show;

    public BucketListAdapter(Context context, List<Bucketlist> items, boolean achieved) {
        mBucketList = items;
        mContext = context;
        show = achieved;
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
        holder.btnAchieve.setTag(bucketList);
        holder.tvBucketDescription.setText(bucketList.getName());
        int id = mContext.getResources().getIdentifier("com.example.buckit:drawable/cat_" + bucketList.getCategory().toLowerCase().substring(0, 4), null, null);
        Log.d("category", String.valueOf(id));
        Glide.with(mContext)
                .load(id)
                .fitCenter()
                .bitmapTransform(new RoundedCornersTransformation(mContext, 20, 0))
                .into(holder.ivCategoryImage);

        if (show = false){
            holder.btnAchieve.setVisibility(View.INVISIBLE);
            holder.btnBookItem.setVisibility(View.INVISIBLE);

        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBucketDescription;
        public ImageView ivCategoryImage;
        public ImageButton btnBookItem;
        public ImageButton btnAchieve;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvBucketDescription = itemView.findViewById(R.id.tvBucketDescription);
            btnBookItem = itemView.findViewById(R.id.btnBookItem);
            btnAchieve = itemView.findViewById(R.id.btnAchievedItem);


            btnAchieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bucketlist item = (Bucketlist) v.getTag();
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

        }

    }

}

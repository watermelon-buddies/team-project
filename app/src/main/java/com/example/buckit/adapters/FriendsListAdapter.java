package com.example.buckit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.shape.RoundedCornerTreatment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.Bucketlist;
import com.example.buckit.models.User;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<User> mFriendsList;
    private Context mContext;

    public FriendsListAdapter(List<User> items, Context context) {
        mFriendsList = items;
        mContext = context;
    }

    User friend;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.view_friends_item_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position

        friend = mFriendsList.get(position);
        holder.tvUsername.setText(friend.getUsername());
        if (friend.getProfilePic() != null){
            Glide.with(mContext)
                    .load(friend.getProfilePic().getUrl())
                    .into(holder.ivProfilePicture);
        }



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
        return mFriendsList.size();
    }
    // for each row, inflate the alyout and cache references intop view

    public void clear() {
        mFriendsList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        mFriendsList.addAll(list);
        notifyDataSetChanged();
    }

    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvUsername;
        public ImageView ivProfilePicture;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);

        }

        @Override
        public void onClick(View v) {

        }

    }
}
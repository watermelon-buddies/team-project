package com.example.buckit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.buckit.models.User.KEY_FRIENDS;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<User> mFriendsList;
    private Context mContext;
    private Boolean mShow;
    private ParseUser currentUser;
    private String id;

    public FriendsListAdapter(List<User> items, Context context, boolean showButton, ParseUser user) {
        mFriendsList = items;
        mContext = context;
        mShow = showButton;
        currentUser = user;
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position
        friend = mFriendsList.get(position);
        Log.d("Friend", friend.getUsername());
        holder.tvUsername.setText(friend.getUsername());
        if (friend.getProfilePic() != null){
            Glide.with(mContext)
                    .load(friend.getProfilePic().getUrl())
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 60, 60))
                    .into(holder.ivProfilePicture);
        }
        else {
            Glide.with(mContext)
                    .load(R.drawable.no_profile)
                    .centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 60, 60))
                    .into(holder.ivProfilePicture);
        }

        if (mShow == true){
            holder.fabAddFriends.setVisibility(View.VISIBLE);
        }
        else {
            holder.fabAddFriends.setVisibility(View.GONE);
        }
        holder.fabAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Log.d("Click", "Working");
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                        User user = mFriendsList.get(position);
                        Log.d("Click", user.toString());
                        currentUser.add(KEY_FRIENDS, user);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("Friends", "Added a new friend!");
                                } else {
                                    Log.d("Friends", "Failed to add!");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
            }
        });

    }

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
        public FloatingActionButton fabAddFriends;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            fabAddFriends = itemView.findViewById(R.id.fabAddFriends);
            fabAddFriends.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

        }

    }
}
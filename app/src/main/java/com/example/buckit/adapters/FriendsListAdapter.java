package com.example.buckit.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.FriendInvite;
import com.example.buckit.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<User> mFriendsList;
    private Context mContext;
    private Boolean mShow;
    private ParseUser currentUser;
    private String id;
    private Boolean mScheduler;


    public FriendsListAdapter(List<User> items, Context context, boolean showButton, boolean scheduler, ParseUser user) {
        mFriendsList = items;
        mContext = context;
        mShow = showButton;
        mScheduler = scheduler;
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
        friend = (User) mFriendsList.get(position);
        Log.d("Friend", friend.getUsername());
        holder.tvUsername.setText(friend.getUsername());
        if (friend.getProfilePic() != null) {
            Glide.with(mContext)
                    .load(friend.getProfilePic().getUrl())
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(holder.ivProfilePicture);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_profile)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(holder.ivProfilePicture);
        }

        if (mShow == true) {
            holder.fabAddFriends.setVisibility(View.VISIBLE);
        } else {
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
                    if (mScheduler) {
                        Intent friendSelected = new Intent();
                        friendSelected.putExtra("selected_user", user.getUsername());
                        ((Activity) mContext).setResult(((Activity)mContext).RESULT_OK, friendSelected);
                        ((Activity) mContext).finish();
                    } else {


                        Log.d("Click", user.toString());
                        createFriendRequest(currentUser, user);
                        mFriendsList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void createFriendRequest(ParseUser inviter, ParseUser invited) {
        FriendInvite newInvitation = new FriendInvite();
        newInvitation.setInviter(inviter);
        newInvitation.setInvited(invited);
        // Status 2 meaning friend request not accepter or rejected yet
        newInvitation.setStatus(2);
        newInvitation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("Invitation", "Sent successfully");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

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
        public Button fabAddFriends;

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
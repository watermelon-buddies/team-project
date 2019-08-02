package com.example.buckit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.activities.SelectTime;
import com.example.buckit.activities.ViewFriends;
import com.example.buckit.models.FriendInvite;
import com.example.buckit.models.User;
import com.example.buckit.models.UserInvite;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.buckit.models.User.KEY_FRIENDS;
import static com.example.buckit.models.User.KEY_PROFILE_PICTURE;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private List<FriendInvite> mRequests;
    private Context mContext;
    private FriendInvite currRequest;
    private ArrayList<User> mFriendsList;
    private FriendsListAdapter mFriendsAdapter;
    ParseUser currUser;

    public FriendRequestsAdapter(List<FriendInvite> requests, Context context, ArrayList<User> friends, FriendsListAdapter friendsAdapter) {
        mRequests = requests;
        mContext = context;
        mFriendsAdapter = friendsAdapter;
        mFriendsList = friends;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        currUser = ParseUser.getCurrentUser();
        View pendingInvitesView = inflater.inflate(R.layout.friend_requests_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(pendingInvitesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position

        currRequest = mRequests.get(position);
        holder.rootView.setTag(currRequest);
        try {
            holder.tvRequestUsername.setText(currRequest.getInviter().fetchIfNeeded().getUsername());
            if (currRequest.getInviter().fetchIfNeeded().getParseFile(KEY_PROFILE_PICTURE) != null) {
                Glide.with(mContext)
                        .load(currRequest.getInviter().fetchIfNeeded().getParseFile(KEY_PROFILE_PICTURE).getUrl())
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(holder.ivRequestProfilePic);
            }
            else {
                Glide.with(mContext)
                        .load(R.drawable.no_profile)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(holder.ivRequestProfilePic);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return mRequests.size();
    }



    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View rootView;
        public TextView tvRequestUsername;
        public ImageView ivRequestProfilePic;
        public Button btnAcceptFriend;
        public Button btnDeclineFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tvRequestUsername = itemView.findViewById(R.id.tvRequestUsername);
            ivRequestProfilePic = itemView.findViewById(R.id.ivRequestProfilePic);
            btnAcceptFriend = itemView.findViewById(R.id.btnAcceptFriend);
            btnDeclineFriend = itemView.findViewById(R.id.btnDeclineFriend);
            btnAcceptFriend.setOnClickListener(this);
            btnDeclineFriend.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FriendInvite invite = (FriendInvite) rootView.getTag();
            if (v.getId() == btnAcceptFriend.getId()){
                invite.setStatus(1);
                currUser.add(KEY_FRIENDS, invite.getInviter());
                currUser.saveInBackground();
                mFriendsList.add(0, (User) invite.getInviter());
                mFriendsAdapter.notifyDataSetChanged();
            }
            else if(v.getId() == btnDeclineFriend.getId()){
                invite.setStatus(0);
            }
            invite.saveInBackground();
            mRequests.remove(invite);
            notifyDataSetChanged();
        }

    }
}

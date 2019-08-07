package com.example.buckit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.models.FriendInvite;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.buckit.models.User.KEY_PROFILE_PICTURE;

public class PendingSentRequestsAdapter extends RecyclerView.Adapter<PendingSentRequestsAdapter.ViewHolder> {

    private List<FriendInvite> mPendingRequests;
    private Context mContext;
    private FriendInvite currPendingRequest;
    ParseUser currUser;

    public PendingSentRequestsAdapter(List<FriendInvite> requests, Context context) {
        mPendingRequests = requests;
        mContext = context;
    }

    @NonNull
    @Override
    public PendingSentRequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        currUser = ParseUser.getCurrentUser();
        View pendingInvitesView = inflater.inflate(R.layout.pending_sent_friend_requests_item_view, parent, false);
        PendingSentRequestsAdapter.ViewHolder viewHolder = new ViewHolder(pendingInvitesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currPendingRequest = mPendingRequests.get(position);
        Log.d("Pending request", currPendingRequest.toString());
        try {
            String username = currPendingRequest.getInvited().fetchIfNeeded().getUsername();
            holder.tvRequestUsername.setText(username.substring(0, username.indexOf(" ")));
                Log.d("Invited", currPendingRequest.getInvited().fetchIfNeeded().getUsername());
                if (currPendingRequest.getInvited().fetchIfNeeded().getParseFile(KEY_PROFILE_PICTURE) != null) {
                    Glide.with(mContext)
                            .load(currPendingRequest.getInvited().fetchIfNeeded().getParseFile(KEY_PROFILE_PICTURE).getUrl())
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
        return mPendingRequests.size();
    }

    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView tvRequestUsername;
        public ImageView ivRequestProfilePic;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRequestUsername = itemView.findViewById(R.id.tvSentRequestProfilePic);
            ivRequestProfilePic = itemView.findViewById(R.id.ivSentRequestProfilePic);
        }

    }
}
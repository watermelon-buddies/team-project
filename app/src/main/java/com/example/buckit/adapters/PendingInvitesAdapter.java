package com.example.buckit.adapters;

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
import android.widget.TextView;

import com.example.buckit.R;
import com.example.buckit.activities.SelectTime;
import com.example.buckit.models.UserInvite;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

public class PendingInvitesAdapter extends RecyclerView.Adapter<PendingInvitesAdapter.ViewHolder> {

    private List<UserInvite> mUserInvites;
    private boolean isPending;
    private Context mContext;
    private UserInvite currInvite;
    private HashMap<String, Integer> mUserCal;
    public static final int SELECT_TIME_REQUEST_CODE = 20;

    public PendingInvitesAdapter(List<UserInvite> items, HashMap<String, Integer> userCal, Context context, boolean pendingInvites) {
        mUserInvites = items;
        mUserCal = userCal;
        mContext = context;
        isPending = pendingInvites;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View pendingInvitesView = inflater.inflate(R.layout.pending_invite_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(pendingInvitesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position

        currInvite = mUserInvites.get(position);
        holder.tvWhat.setText(currInvite.getTitle());
        ParseUser getFriend = currInvite.getCreator();
        if(getFriend == ParseUser.getCurrentUser()){
            getFriend = currInvite.getInvited();
        }
        try{
            holder.tvWho.setText("with " + getFriend.fetchIfNeeded().getUsername());
        } catch(ParseException e){
            e.printStackTrace();
        }
        holder.tvWhere.setText("at " + currInvite.getLocation());


    }



    @Override
    public int getItemCount() {
        return mUserInvites.size();
    }

    public void removeData(int position) {
        mUserInvites.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());
    }



    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvWhat;
        public TextView tvWho;
        public TextView tvWhere;
        public Button btnAccept;
        public Button btnDecline;
        public View buttonDivider;
        public View horizontalDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWhat = itemView.findViewById(R.id.tvWhat);
            tvWho = itemView.findViewById(R.id.tvWho);
            tvWhere = itemView.findViewById(R.id.tvWhere);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            buttonDivider = itemView.findViewById(R.id.buttonDivider);
            horizontalDivider = itemView.findViewById(R.id.horizontalDivider);
            if(!isPending){
                btnAccept.setVisibility(View.GONE);
                btnDecline.setVisibility(View.GONE);
                buttonDivider.setVisibility(View.GONE);
                horizontalDivider.setVisibility(View.GONE);
            }
            btnAccept.setOnClickListener(this);
            btnDecline.setOnClickListener(this);
        }

//        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            UserInvite currInvite = mUserInvites.get(position);
            if(isPending){
                if(v.getId() == btnAccept.getId()){
                    Intent selectTime = new Intent(mContext, SelectTime.class);
                    selectTime.putExtra("InviteTimes", currInvite.getMeetTimes().toString());
                    selectTime.putExtra("userCal", mUserCal);
                    selectTime.putExtra("duration", currInvite.getDuration());
                    selectTime.putExtra("position", position);
                    selectTime.putExtra("title", currInvite.getTitle());
                    selectTime.putExtra("location", currInvite.getLocation());
                    ((Activity) mContext).startActivityForResult(selectTime, SELECT_TIME_REQUEST_CODE);

                } else if(v.getId() == btnDecline.getId()){
                    currInvite.setAccepted(false);
                    currInvite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("Pending Invites", "Event Declined");
                            } else {
                                Log.d("Pending Invites", "Failed to decline");
                                e.printStackTrace();
                            }
                        }
                    });
                    removeData(position);

                }
            }
        }



    }
}
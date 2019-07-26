package com.example.buckit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.HashMap;
import java.util.List;

public class PendingInvitesAdapter extends RecyclerView.Adapter<PendingInvitesAdapter.ViewHolder> {

    private List<UserInvite> mUserInvites;
    private Context mContext;
    private UserInvite currInvite;
    private HashMap<String, Integer> mUserCal;

    public PendingInvitesAdapter(List<UserInvite> items, HashMap<String, Integer> userCal, Context context) {
        mUserInvites = items;
        mUserCal = userCal;
        mContext = context;
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
        ParseUser creator = currInvite.getCreator();
        try{
            holder.tvWho.setText("with " + creator.fetchIfNeeded().getUsername());
        } catch(ParseException e){
            e.printStackTrace();
        }
        holder.tvWhere.setText("at " + currInvite.getLocation());


    }



    @Override
    public int getItemCount() {
        return mUserInvites.size();
    }



    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvWhat;
        public TextView tvWho;
        public TextView tvWhere;
        public Button btnAccept;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWhat = itemView.findViewById(R.id.tvWhat);
            tvWho = itemView.findViewById(R.id.tvWho);
            tvWhere = itemView.findViewById(R.id.tvWhere);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnAccept.setOnClickListener(this);
        }

//        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            UserInvite currInvite = mUserInvites.get(position);
            if(v.getId() == btnAccept.getId()){
                Intent selectTime = new Intent(mContext, SelectTime.class);
                selectTime.putExtra("InviteTimes", currInvite.getMeetTimes().toString());
                selectTime.putExtra("userCal", mUserCal);
                mContext.startActivity(selectTime);

            }

        }

    }
}
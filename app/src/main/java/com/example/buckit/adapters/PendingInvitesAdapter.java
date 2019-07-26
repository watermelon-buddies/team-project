package com.example.buckit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.buckit.R;
import com.example.buckit.models.UserInvite;

import java.util.List;

public class PendingInvitesAdapter extends RecyclerView.Adapter<PendingInvitesAdapter.ViewHolder> {

    private List<UserInvite> mUserInvites;
    private Context mContext;
    private UserInvite currInvite;

    public PendingInvitesAdapter(List<UserInvite> items, Context context) {
        mUserInvites = items;
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
        holder.tvWho.setText(currInvite.getInvited().getUsername());
        holder.tvWhere.setText(currInvite.getLocation());


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

        public ViewHolder(View itemView) {
            super(itemView);
            tvWhat = itemView.findViewById(R.id.tvWhat);
            tvWho = itemView.findViewById(R.id.tvWho);
            tvWhere = itemView.findViewById(R.id.tvWhere);

        }

//        @Override
        public void onClick(View v) {

        }

    }
}
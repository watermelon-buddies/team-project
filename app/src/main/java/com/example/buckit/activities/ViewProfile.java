package com.example.buckit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.example.buckit.R;
import com.example.buckit.adapters.PendingInvitesAdapter;
import com.example.buckit.models.UserInvite;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.buckit.adapters.PendingInvitesAdapter.SELECT_TIME_REQUEST_CODE;

public class ViewProfile extends AppCompatActivity {

    Unbinder unbinder;
    ArrayList<UserInvite> mPendingInvites;
    ArrayList<UserInvite> mComingUpInvites;
    PendingInvitesAdapter mPendingInvitesAdapter;
    PendingInvitesAdapter mComingUpInvitesAdapter;
    @BindView(R.id.rvPendingInvites) RecyclerView rvPendingInvites;
    @BindView(R.id.rvComingUp) RecyclerView rvComingUp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_drawrer_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.view_profile);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        HashMap<String, Integer> userCal = (HashMap<String, Integer>) getIntent().getSerializableExtra("userCal");
        mPendingInvites = new ArrayList<UserInvite>();
        mComingUpInvites = new ArrayList<>();
        mPendingInvitesAdapter = new PendingInvitesAdapter(mPendingInvites, userCal, this, true);
        mComingUpInvitesAdapter = new PendingInvitesAdapter(mComingUpInvites, userCal, this, false);
        rvPendingInvites.setAdapter(mPendingInvitesAdapter);
        rvComingUp.setAdapter(mComingUpInvitesAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPendingInvites.setLayoutManager(linearLayoutManager);
        rvComingUp.setLayoutManager(linearLayoutManager2);
        populatePendingInvites();
        populateComingUpInvites(true);
    }


    protected void populatePendingInvites() {
        final UserInvite.Query userInviteQuery = new UserInvite.Query();
        userInviteQuery.getTop().withInvited().withAccepted();
        userInviteQuery.whereEqualTo(UserInvite.KEY_INVITED, ParseUser.getCurrentUser());
        userInviteQuery.whereEqualTo(UserInvite.KEY_ACCEPTED, false);
        userInviteQuery.findInBackground(new FindCallback<UserInvite>() {
            @Override
            public void done(List<UserInvite> object, ParseException e) {
                if (e == null) {
                    mPendingInvites.clear();
                    mPendingInvites.addAll(object);
                    mPendingInvitesAdapter.notifyDataSetChanged();
                    Log.d("Timeline Activity", "Successfully loaded posts!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void populateComingUpInvites(final boolean invited){
        final UserInvite.Query comingUpInviteQuery = new UserInvite.Query();
        comingUpInviteQuery.getTop().withAccepted().withInvited();
        if(invited){
            comingUpInviteQuery.whereEqualTo(UserInvite.KEY_INVITED, ParseUser.getCurrentUser());
        } else {
            comingUpInviteQuery.whereEqualTo(UserInvite.KEY_CREATOR, ParseUser.getCurrentUser());
        }
        comingUpInviteQuery.whereEqualTo(UserInvite.KEY_ACCEPTED, true);
        comingUpInviteQuery.findInBackground(new FindCallback<UserInvite>() {
            @Override
            public void done(List<UserInvite> objects, ParseException e) {
                if(e == null){
                    if(invited){
                        mComingUpInvites.clear();
                    }
                    mComingUpInvites.addAll(objects);
                    mComingUpInvitesAdapter.notifyDataSetChanged();
                    if(invited){
                        populateComingUpInvites(false);
                    }
                    Log.d("Invites", "Successfully loaded events coming up!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SELECT_TIME_REQUEST_CODE){
            final Integer invitePosition = data.getExtras().getInt("position", 0);
            final UserInvite curr = mPendingInvites.get(invitePosition);
            String finalTime = data.getStringExtra("final time");
            curr.setFinalTime(finalTime);
            curr.setAccepted(true);
            curr.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Invite Booked", Toast.LENGTH_SHORT).show();
                        Log.d("Accept invite", "Accept invite successful");
                        mPendingInvitesAdapter.removeData(invitePosition);
                        mComingUpInvites.add(curr);
                        mComingUpInvitesAdapter.notifyItemInserted(mComingUpInvites.size()-1);
                    } else {
                        Log.d("Accept invite", "Failed in booking invite");
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}

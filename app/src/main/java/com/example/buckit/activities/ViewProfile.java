package com.example.buckit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.example.buckit.R;
import com.example.buckit.adapters.PendingInvitesAdapter;
import com.example.buckit.models.UserInvite;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ViewProfile extends AppCompatActivity {

    Unbinder unbinder;
    ArrayList<UserInvite> mPendingInvites;
    PendingInvitesAdapter mPendingInvitesAdapter;
    @BindView(R.id.rvFriendsList) RecyclerView rvPendingInvites;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_drawrer_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.view_profile);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        HashMap<String, Integer> userCal = (HashMap<String, Integer>) getIntent().getSerializableExtra("userCal");
        mPendingInvites = new ArrayList<UserInvite>();
        mPendingInvitesAdapter = new PendingInvitesAdapter(mPendingInvites, userCal, this);
        rvPendingInvites.setAdapter(mPendingInvitesAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPendingInvites.setLayoutManager(linearLayoutManager);
        populateInvites();
    }

    protected void populateInvites() {
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
}

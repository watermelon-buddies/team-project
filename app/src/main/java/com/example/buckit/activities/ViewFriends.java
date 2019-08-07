package com.example.buckit.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.LoginActivity;
import com.example.buckit.R;
import com.example.buckit.adapters.FriendRequestsAdapter;
import com.example.buckit.adapters.FriendsListAdapter;
import com.example.buckit.adapters.PendingSentRequestsAdapter;
import com.example.buckit.fragments.AddFriendsActivity;
import com.example.buckit.models.FriendInvite;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.buckit.models.User.KEY_FRIENDS;
import static com.example.buckit.models.User.KEY_PROFILE_PICTURE;

public class ViewFriends extends AppCompatActivity {

    ArrayList<User> mFriendsList;
    FriendsListAdapter mFriendsAdapter;
    ArrayList<FriendInvite> mRequestsList;
    FriendRequestsAdapter mRequestsAdapter;
    ArrayList<FriendInvite> mPendingRequestsList;
    PendingSentRequestsAdapter mPendingRequestsAdapter;
    FriendsListAdapter usersAdapter;
    ArrayList<User> usersList;
    @BindView(R.id.btnAddFriends)
    TextView btnAddFriends;
    @BindView(R.id.rvFriendsList)
    RecyclerView rvFriendsList;
    @BindView(R.id.rvFriendRequests) RecyclerView rvRequestsList;
    @BindView(R.id.drawer_layout)
    DrawerLayout leftDrawer;
    @BindView(R.id.nav_view)
    NavigationView leftDrawerNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ivLogo) ImageView logo;
    public HashMap<String, Integer> userEvents;
    ParseUser currentUser;
    @BindView(R.id.tvNoRequests) TextView tvNoRequests;
    @BindView(R.id.tvCurrentFriends) TextView tvCurrentFriends;
    @BindView(R.id.rvPendingRequests) RecyclerView rvPendingRequests;
    @BindView(R.id.tvPendingRequests) TextView tvPendingRequests;
    @BindView(R.id.tvNoPendingRequests) TextView tvNoPendingRequests;
    @BindView(R.id.viewFriendsConstraint)
    ConstraintLayout viewFriendsConstraint;
    @BindView(R.id.tvFriendRequests) TextView tvFriendRequests;
    Boolean scheduler = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
        userEvents = (HashMap<String, Integer>) getIntent().getSerializableExtra("userCal");
        ParseACL acl = new ParseACL(currentUser);
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        currentUser.setACL(acl);
        JSONArray friends = currentUser.getJSONArray(KEY_FRIENDS);
        if (friends == null){
            ArrayList<String> friendsEmptyList = new ArrayList<>();
            friendsEmptyList.add("Beam");
            currentUser.put(KEY_FRIENDS, friendsEmptyList);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("Home Activity", "Create a new post success!");
                    } else {
                        Log.d("Home Activity", "Failed in creating a post!");
                        e.printStackTrace();
                    }
                }
            });
            startActivity(new Intent(ViewFriends.this, AddFriendsActivity.class));
            this.finish();
        }
        setContentView(R.layout.activities_drawrer_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.view_friends_fragment);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        mFriendsList = new ArrayList<User>();
        if(getIntent().getBooleanExtra("scheduler", false) == true){
            String userToInvite = "";
            btnAddFriends.setVisibility(View.GONE);
            scheduler = true;
            toolbar.setVisibility(View.GONE);
            logo.setVisibility(View.GONE);
            rvRequestsList.setVisibility(View.GONE);
            tvNoRequests.setVisibility(View.GONE);
            tvFriendRequests.setVisibility(View.GONE);
            tvNoPendingRequests.setVisibility(View.GONE);
            tvPendingRequests.setVisibility(View.GONE);
            rvPendingRequests.setVisibility(View.GONE);
            mFriendsAdapter = new FriendsListAdapter(mFriendsList, this, true, true, currentUser);
        } else {
            mFriendsAdapter = new FriendsListAdapter(mFriendsList, this, false, false, currentUser);
        }
        rvFriendsList.setAdapter(mFriendsAdapter);
        // associate the LinearLayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManagerForFriends = new LinearLayoutManager(this);
        rvFriendsList.setLayoutManager(linearLayoutManagerForFriends);
        updateFriends(currentUser);
        mRequestsList = new ArrayList<FriendInvite>();
        mRequestsAdapter = new FriendRequestsAdapter(mRequestsList, this, mFriendsList, mFriendsAdapter);
        rvRequestsList.setAdapter(mRequestsAdapter);
        final LinearLayoutManager linearLayoutManagerForRequests = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRequestsList.setLayoutManager(linearLayoutManagerForRequests);
        populateInvites();
        mPendingRequestsList = new ArrayList<FriendInvite>();
        mPendingRequestsAdapter = new PendingSentRequestsAdapter(mPendingRequestsList, this);
        rvPendingRequests.setAdapter(mPendingRequestsAdapter);
        final LinearLayoutManager linearLayoutManagerForPending = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPendingRequests.setLayoutManager(linearLayoutManagerForPending);
        populatePendingRequests();
        btnAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewFriends.this, AddFriendsActivity.class));
                finish();
            }
        });
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, leftDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        leftDrawer.addDrawerListener(toggle);
        customView();
        toggle.syncState();
        leftDrawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_logout) {
                    ParseUser.logOut();
                    Intent logoutIntent = new Intent(ViewFriends.this, LoginActivity.class);
                    startActivity(logoutIntent);
                    finish();
                } else if (id == R.id.nav_view_friends) {
                    startActivity(new Intent(ViewFriends.this, ViewFriends.class));
                } else if (id == R.id.nav_profile) {
                    Intent profileView = new Intent(ViewFriends.this, ViewProfile.class);
                    profileView.putExtra("userCal", userEvents);
                    startActivity(profileView);
                }
                leftDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (leftDrawer.isDrawerOpen(GravityCompat.START)) {
            ImageView ivUserProfilePic = findViewById(R.id.ivUserProfilePic);
            Glide.with(this)
                    .load(currentUser.getParseFile(KEY_PROFILE_PICTURE).getUrl())
                    .into(ivUserProfilePic);
            leftDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    protected void updateFriends(final ParseUser user) {
        FriendInvite.Query checkInvitations = new FriendInvite.Query();
        checkInvitations.getAccepted();
        checkInvitations.findInBackground(new FindCallback<FriendInvite>() {
            @Override
            public void done(List<FriendInvite> objects, ParseException e) {
                if (e == null){
                    if (objects != null && objects.size()>0) {
                        for (int i = 0; i<objects.size(); i++){
                            FriendInvite acceptedInvitations = objects.get(i);
                            if (acceptedInvitations.getInviter().getObjectId().equals(user.getObjectId())){
                                ParseUser friend = acceptedInvitations.getInvited();
                                currentUser.add(KEY_FRIENDS, friend);
                                currentUser.saveInBackground();
                                try {
                                    acceptedInvitations.delete();
                                    acceptedInvitations.saveInBackground();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                    populateFriends();
                }
            }
        });


    }

    public void populateFriends() {
        User.Query userQuery = new User.Query();
        userQuery.getTop().withFriends();
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> object, ParseException e) {
                if (e == null) {
                    if (object != null && object.size() > 0){
                        ParseObject user = object.get(0);  // only one match expected
                        // now get the user's friends
                        List<User> friends = user.getList("friends");
                        mFriendsList.clear();
                        mFriendsList.addAll(friends);
                        mFriendsAdapter.notifyDataSetChanged();
                        Log.d("Timeline Activity", "Successfully loaded posts!");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateInvites() {
        FriendInvite.Query requestsQuery = new FriendInvite.Query();
        requestsQuery.getFriendRequests(currentUser);
        requestsQuery.getUnansweredRequests();
        requestsQuery.findInBackground(new FindCallback<FriendInvite>() {
            @Override
            public void done(List<FriendInvite> requests, ParseException e) {
                if (e == null) {
                    if (requests != null && requests.size()>0) {
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(viewFriendsConstraint);
                        constraintSet.connect(R.id.tvPendingRequests,ConstraintSet.TOP, R.id.rvFriendRequests,ConstraintSet.BOTTOM,16);
                        constraintSet.applyTo(viewFriendsConstraint);
                        tvNoRequests.setVisibility(View.INVISIBLE);
                        mRequestsList.clear();
                        mRequestsList.addAll(requests);
                        mRequestsAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populatePendingRequests() {
        FriendInvite.Query requestsQuery = new FriendInvite.Query();
        requestsQuery.getSentRequests(currentUser);
        requestsQuery.getUnansweredRequests();
        Log.d("Pending requests", "Works");
        requestsQuery.findInBackground(new FindCallback<FriendInvite>() {
            @Override
            public void done(List<FriendInvite> pendingRequests, ParseException e) {
                if (e == null) {
                    if (pendingRequests != null && pendingRequests.size()>0) {
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(viewFriendsConstraint);
                        constraintSet.connect(R.id.tvCurrentFriends,ConstraintSet.TOP, R.id.rvPendingRequests,ConstraintSet.BOTTOM,16);
                        constraintSet.applyTo(viewFriendsConstraint);
                        tvNoPendingRequests.setVisibility(View.INVISIBLE);
                        Log.d("Pending requests", pendingRequests.toString());
                        mPendingRequestsList.clear();
                        mPendingRequestsList.addAll(pendingRequests);
                        Log.d("Pending requests", mPendingRequestsList.toString());
                        mPendingRequestsAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void customView(){
        View headerLayout = leftDrawerNavigationView.getHeaderView(0);
        headerLayout.setBackgroundColor(R.color.bright_blue);
        User user = (User) ParseUser.getCurrentUser();
        TextView tvUsername = headerLayout.findViewById(R.id.tvUsername);
        ImageView ivUserProfilePic = headerLayout.findViewById(R.id.ivUserProfilePic);
        tvUsername.setText(user.getUsername());
        if (user.getProfilePic() != null){
            Glide.with(this)
                    .load(user.getProfilePic().getUrl())
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivUserProfilePic);
        }
        else{
            Glide.with(this)
                    .load(R.drawable.no_profile)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(ivUserProfilePic);
        }
    }

}

package com.example.buckit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.buckit.R;
import com.example.buckit.adapters.FriendsListAdapter;
import com.example.buckit.models.FriendInvite;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddFriends extends AppCompatActivity {

    Unbinder unbinder;
    FriendsListAdapter usersAdapter;
    ArrayList<User> usersList;
    @BindView(R.id.rvAddFriends)
    RecyclerView rvAddFriends;
    @BindView(R.id.ivCloseMenu)
    ImageButton ivCloseMenu;
    ParseUser user;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends_main);
        user = ParseUser.getCurrentUser();
        ButterKnife.bind(this);
        usersList = new ArrayList<User>();
        usersAdapter = new FriendsListAdapter(usersList, this, true, false, user);
        rvAddFriends.setAdapter(usersAdapter);
        final LinearLayoutManager addLinearLayoutManager = new LinearLayoutManager(this);
        findUsersAlreadyRequested();
        rvAddFriends.setLayoutManager(addLinearLayoutManager);
        ivCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AddFriends.this, ViewFriends.class));
            }
        });
    }


    private void findUsersAlreadyRequested() {
        final List<String> requestedIds = new ArrayList<>();
        final FriendInvite.Query sentOutInvited = new FriendInvite.Query();
        sentOutInvited.findInBackground(new FindCallback<FriendInvite>() {
            @Override
            public void done(List<FriendInvite> objects, ParseException e) {
                if (e == null) {
                    if (objects != null && objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            FriendInvite invitation = objects.get(i);
                            String id1 = invitation.getInviter().getObjectId();
                            String id2 = user.getObjectId();
                            Log.d("Id", id1 + " " + id2);
                            if (id1.equals(id2)) {
                                String id = invitation.getInvited().getObjectId();
                                requestedIds.add(id);
                            }
                        }
                    }
                    findAlreadyFriends(requestedIds);
                }
            }
        });

    }

    protected void findAlreadyFriends(final List<String> requestedIds) {
        User.Query friendsQuery = new User.Query();
        friendsQuery.withFriends();
        friendsQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        friendsQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (e == null) {
                    if (users != null && users.size() > 0) {
                        ParseObject mainUser = users.get(0);
                        List<User> friends = mainUser.getList("friends");
                        List<String> alreadyRequestedIds = new ArrayList<>();
                        for (User friend : friends) {
                            alreadyRequestedIds.add(friend.getObjectId());
                        }
                        alreadyRequestedIds.addAll(requestedIds);
                        populateUsers(alreadyRequestedIds);
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void populateUsers(List<String> alreadyRequestedIds) {
        User.Query userQuery = new User.Query();
        userQuery.whereNotEqualTo("objectId", user.getObjectId());
        userQuery.whereNotContainedIn("objectId", alreadyRequestedIds);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(final List<User> object, ParseException e) {
                if (e == null) {
                    usersList.clear();
                    usersList.addAll(object);
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}



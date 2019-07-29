package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.buckit.R;
import com.example.buckit.adapters.FriendsListAdapter;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddFriendsActivity extends AppCompatActivity {

    Unbinder unbinder;
    FriendsListAdapter usersAdapter;
    ArrayList<User> usersList;
    @BindView(R.id.rvAddFriends)
    RecyclerView rvAddFriends;
    @BindView(R.id.ivCloseMenu) ImageButton ivCloseMenu;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends_main);
        ParseUser user = ParseUser.getCurrentUser();
        ButterKnife.bind(this);
        usersList = new ArrayList<User>();
        usersAdapter = new FriendsListAdapter(usersList, this, true, user);
        rvAddFriends.setAdapter(usersAdapter);
        final LinearLayoutManager addLinearLayoutManager = new LinearLayoutManager(this);
        populateUsers();
        rvAddFriends.setLayoutManager(addLinearLayoutManager);
        ivCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void populateUsers() {
        final User.Query userQuery = new User.Query();
        userQuery.getTop();
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> object, ParseException e) {
                if (e == null) {
                    usersList.clear();
                    usersList.addAll(object);
                    usersAdapter.notifyDataSetChanged();
                    Log.d("Timeline Activity", "Successfully loaded posts!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}

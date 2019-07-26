package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.example.buckit.R;
import com.example.buckit.adapters.FriendsListAdapter;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ViewFriendsFragment extends AppCompatActivity {

    Unbinder unbinder;
    ArrayList<User> mFriendsList;
    FriendsListAdapter mFriendsAdapter;
    @BindView(R.id.rvFriendsList)
    RecyclerView rvFriendsList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_drawrer_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.view_friends_fragment);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        mFriendsList = new ArrayList<User>();
        mFriendsAdapter = new FriendsListAdapter(mFriendsList, this);
        rvFriendsList.setAdapter(mFriendsAdapter);
        // associate the LinearLayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFriendsList.setLayoutManager(linearLayoutManager);
        populateFriends();
    }

    protected void populateFriends() {
        final User.Query userQuery = new User.Query();
        userQuery.getTop();
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> object, ParseException e) {
                if (e == null) {
                    mFriendsList.clear();
                    mFriendsList.addAll(object);
                    mFriendsAdapter.notifyDataSetChanged();
                    Log.d("Timeline Activity", "Successfully loaded posts!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

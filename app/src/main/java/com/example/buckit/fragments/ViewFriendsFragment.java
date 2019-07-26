package com.example.buckit.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.buckit.R;
import com.example.buckit.adapters.FriendsListAdapter;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.buckit.fragments.EventsExploreFragment.KEY_SELECTED_CATEGORIES;
import static com.example.buckit.models.User.KEY_FRIENDS;
import static com.parse.Parse.getApplicationContext;

public class ViewFriendsFragment extends AppCompatActivity {

    Unbinder unbinder;
    ArrayList<User> mFriendsList;
    FriendsListAdapter mFriendsAdapter;
    FriendsListAdapter usersAdapter;
    ArrayList<User> usersList;
    @BindView(R.id.btnAddFriends)
    TextView btnAddFriends;
    @BindView(R.id.rvFriendsList)
    RecyclerView rvFriendsList;
    public View popupView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL(currentUser);
        acl.setPublicReadAccess(true);
        currentUser.setACL(acl);
        JSONArray categories = currentUser.getJSONArray(KEY_FRIENDS);
        if (categories == null){
            ArrayList<String> friendsEmptyList = new ArrayList<>();
            friendsEmptyList.add("Beamlak");
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
        }
        setContentView(R.layout.activities_drawrer_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.view_friends_fragment);
        View inflated = stub.inflate();
        ButterKnife.bind(this);
        mFriendsList = new ArrayList<User>();
        mFriendsAdapter = new FriendsListAdapter(mFriendsList, this, false, currentUser);
        rvFriendsList.setAdapter(mFriendsAdapter);
        // associate the LinearLayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFriendsList.setLayoutManager(linearLayoutManager);
        populateFriends();
        btnAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewFriendsFragment.this, AddFriendsActivity.class));
            }
        });
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

package com.example.buckit.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.buckit.adapters.BucketListAdapter;
import com.example.buckit.R;
import com.example.buckit.models.Bucketlist;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BucketListFragment extends Fragment {

    @BindView(R.id.rvBucketList)  RecyclerView rvBucketList;
    @BindView(R.id.fab)  FloatingActionButton fab;
    @BindView(R.id.swipeContainer)  SwipeRefreshLayout swipeContainer;
    private Unbinder unbinder;
    ArrayList<Bucketlist> mBucketList;
    BucketListAdapter mBucketAdapter;
    boolean mFirstLoad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.activity_bucket_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();

        // Dial contact's number.
        // This shows the dialer with the number, allowing you to explicitly initiate the call.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        mBucketList = new ArrayList<>();
        mBucketAdapter = new BucketListAdapter(getContext(), mBucketList);
        rvBucketList.setAdapter(mBucketAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_red_light);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBucketList.setLayoutManager(linearLayoutManager);
        populateTimeline();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddToBucketList addToBucketList = AddToBucketList.newInstance("Composing Tweet");
        addToBucketList.show(fm, "fragment_compose_tweet");
    }

    public void onFinishEditDialog(String text) {
        populateTimeline();
        // notify the user the operation completed OK
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void populateTimeline() {
        final Bucketlist.Query postQuery = new Bucketlist.Query();
        postQuery.getTop().withUser();
        postQuery.orderByDescending("createdAt");

        postQuery.findInBackground(new FindCallback<Bucketlist>() {
            @Override
            public void done(List<Bucketlist> object, ParseException e) {
                if (e == null) {
                    mBucketList.clear();
                    mBucketList.addAll(object);
                    mBucketAdapter.notifyDataSetChanged();
                    if (mFirstLoad) {
                        rvBucketList.scrollToPosition(0);
                        mFirstLoad = false;
                    }

                    Log.d("Timeline Activity", "Successfully loaded posts!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}

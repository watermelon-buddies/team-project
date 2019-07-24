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

    /*
    Fragment activated when bucket icon on bottom naivation bar is clicked that allows user to see
    their bucket list ideas, add ideas with categories specified and go to scheduler to book
    specific time
     */

    @BindView(R.id.rvBucketList)  RecyclerView rvBucketList;
    @BindView(R.id.ivBlur)  FloatingActionButton addItemFab;
    @BindView(R.id.swipeContainer)  SwipeRefreshLayout bucketRefreshSwipeContainer;
    private Unbinder unbinder;
    ArrayList<Bucketlist> mBucketList;
    BucketListAdapter mBucketAdapter;
    boolean mFirstLoad;

    /* Inflate bucket_list_fragment.xml and bind views using Butterknife */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.bucket_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        /* addItem floating action button for adding new item to the bucket list */
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        mBucketList = new ArrayList<>();
        mBucketAdapter = new BucketListAdapter(getContext(), mBucketList);
        rvBucketList.setAdapter(mBucketAdapter);
        // associate the LinearLayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBucketList.setLayoutManager(linearLayoutManager);
        populateBucket();

        bucketRefreshSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call populate timeline when swipe to refresh is activated
                populateBucket();
                bucketRefreshSwipeContainer.setRefreshing(false);
            }
        });
        bucketRefreshSwipeContainer.setColorSchemeResources(android.R.color.holo_red_light);
    }

    /* Unbind butterknife binded views when fragment is closed*/
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddToBucketList addToBucketList = AddToBucketList.newInstance("Composing Tweet");
        addToBucketList.show(fm, "fragment_compose_tweet");
    }


    protected void populateBucket() {
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

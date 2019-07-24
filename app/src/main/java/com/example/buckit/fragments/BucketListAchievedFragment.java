package com.example.buckit.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buckit.R;
import com.example.buckit.adapters.BucketListAdapter;
import com.example.buckit.models.Bucketlist;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BucketListAchievedFragment extends Fragment {

    Unbinder unbinder;
    ArrayList<Bucketlist> mBucketList;
    BucketListAdapter mBucketAdapter;
    @BindView(R.id.rvBucketList) RecyclerView rvBucketList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.activity_bucket_list_achieved_fragment, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull final View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);

        mBucketList = new ArrayList<>();
        mBucketAdapter = new BucketListAdapter(getContext(), mBucketList);
        rvBucketList.setAdapter(mBucketAdapter);
        // associate the LinearLayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBucketList.setLayoutManager(linearLayoutManager);
        populateBucket();
    }

    /* Unbind butterknife binded views when fragment is closed*/
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected void populateBucket() {
        final Bucketlist.Query bucketQuery = new Bucketlist.Query();
        bucketQuery.getTop().withUser();
        bucketQuery.orderByDescending("createdAt");
        bucketQuery.whereEqualTo("achieved", true);

        bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
            @Override
            public void done(List<Bucketlist> object, ParseException e) {
                if (e == null) {
                    mBucketList.clear();
                    mBucketList.addAll(object);
                    mBucketAdapter.notifyDataSetChanged();
                    Log.d("Timeline Activity", "Successfully loaded posts!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

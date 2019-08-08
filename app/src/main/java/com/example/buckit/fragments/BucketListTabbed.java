package com.example.buckit.fragments;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.buckit.R;
import com.example.buckit.adapters.BucketListAdapter;
import com.example.buckit.models.Bucketlist;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BucketListTabbed extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.btnAchievedBucketTab) Button btnAchievedBucket;
    @BindView(R.id.btnCurrentBucketTab) Button btnCurrentBucket;
    @BindView(R.id.ivHorizontalDivider) ImageView ivHorizontalDivider;
    Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.activity_bucket_list_tabbed, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull final View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment = new BucketListCurrentFragment();
        fragmentManager.beginTransaction().replace(R.id.flBucket, fragment).commit();
        btnAchievedBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new BucketListAchievedFragment();
                fragmentManager.beginTransaction().replace(R.id.flBucket,
                        fragment).commit();
                btnCurrentBucket.setTextColor(getResources().getColor(R.color.grey));
                btnAchievedBucket.setTextColor(getResources().getColor(R.color.bright_blue));
                ivHorizontalDivider.animate().translationXBy(540);

            }
        });
        btnCurrentBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new BucketListCurrentFragment();
                fragmentManager.beginTransaction().replace(R.id.flBucket, fragment).commit();
                btnCurrentBucket.setTextColor(getResources().getColor(R.color.bright_blue));
                btnAchievedBucket.setTextColor(getResources().getColor(R.color.grey));
                ivHorizontalDivider.animate().translationXBy(-540);
            }
        });
    }

    /* Unbind butterknife binded views when fragment is closed*/
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

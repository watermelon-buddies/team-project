package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.buckit.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BucketListTabbed extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.btnAchievedBucketTab) Button btnAchievedBucket;
    @BindView(R.id.btnCurrentBucketTab) Button btnCurrentBucket;
    @BindView(R.id.ivHorizontalDivider) ImageView ivHorizontalDivider;
    Fragment fragment;
    HashMap<String, Integer> userEvents;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.activity_bucket_list_tabbed, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        if (getArguments() != null) {
            userEvents = (HashMap<String, Integer>) getArguments().getSerializable("userEvents");
        }
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull final View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragment = new BucketListCurrentFragment();
        Bundle userCalendar = new Bundle();
        userCalendar.putSerializable("userEvents", userEvents);
        fragment.setArguments(userCalendar);
        fragmentManager.beginTransaction().replace(R.id.flBucket, fragment).commit();
        btnAchievedBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new BucketListAchievedFragment();
                Bundle userCalendar = new Bundle();
                userCalendar.putSerializable("userEvents", userEvents);
                fragment.setArguments(userCalendar);
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
                Bundle userCalendar = new Bundle();
                userCalendar.putSerializable("userEvents", userEvents);
                fragment.setArguments(userCalendar);
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

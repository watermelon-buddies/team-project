package com.example.buckit.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buckit.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SchedulerFragment extends Fragment {

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View schedulerView = inflater.inflate(R.layout.scheduler_fragment, container, false);
        unbinder = ButterKnife.bind(this, schedulerView);
        return schedulerView;
    }

    @Override
    public void onViewCreated(@NonNull View schedulerView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(schedulerView, savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

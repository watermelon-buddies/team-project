package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.buckit.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SchedulerFragment extends Fragment {

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View schedulerView = inflater.inflate(R.layout.activity_scheduler_fragment, container, false);
        unbinder = ButterKnife.bind(this, schedulerView);
        return schedulerView;
    }

    @Override
    public void onViewCreated(@NonNull View schedulerView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(schedulerView, savedInstanceState);
        Button btn1 = schedulerView.findViewById(R.id.btnRecent1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btn2 = schedulerView.findViewById(R.id.btnRecent2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btn3 = schedulerView.findViewById(R.id.btnRecent3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btn4 = schedulerView.findViewById(R.id.btnRecent4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnMonday = schedulerView.findViewById(R.id.btnMonday);
        btnMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnTuesday = schedulerView.findViewById(R.id.btnTuesday);
        btnTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnWednesday = schedulerView.findViewById(R.id.btnWednesday);
        btnWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnThursday = schedulerView.findViewById(R.id.btnThursday);
        btnThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnFriday = schedulerView.findViewById(R.id.btnFriday);
        btnFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnSaturday = schedulerView.findViewById(R.id.btnSaturday);
        btnSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
        Button btnSunday = schedulerView.findViewById(R.id.btnSunday);
        btnSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

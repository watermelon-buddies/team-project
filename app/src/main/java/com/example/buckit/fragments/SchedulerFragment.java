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

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SchedulerFragment extends Fragment {

    private Unbinder unbinder;
    private ArrayList<Button> schedulerButtons;


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
        createButtonArray(schedulerView);
        addListeners();
    }

    private void addListeners() {
        for (Button btn : schedulerButtons) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                    } else {
                        v.setSelected(true);
                    }
                }
            });
        }
    }

    private void createButtonArray(View schedulerView) {
        schedulerButtons = new ArrayList<>();
        Button btnRecent1 = schedulerView.findViewById(R.id.btnRecent1);
        Button btnRecent2 = schedulerView.findViewById(R.id.btnRecent2);
        Button btnRecent3 = schedulerView.findViewById(R.id.btnRecent3);
        Button btnRecent4 = schedulerView.findViewById(R.id.btnRecent4);
        Button btnMonday = schedulerView.findViewById(R.id.btnMonday);
        Button btnTuesday = schedulerView.findViewById(R.id.btnTuesday);
        Button btnWednesday = schedulerView.findViewById(R.id.btnWednesday);
        Button btnThursday = schedulerView.findViewById(R.id.btnThursday);
        Button btnFriday = schedulerView.findViewById(R.id.btnFriday);
        Button btnSaturday = schedulerView.findViewById(R.id.btnSaturday);
        Button btnSunday = schedulerView.findViewById(R.id.btnSunday);
        Button btnMorning = schedulerView.findViewById(R.id.btnMorning);
        Button btnAfternoon = schedulerView.findViewById(R.id.btnAfternoon);
        Button btnEvening = schedulerView.findViewById(R.id.btnEvening);
        Button btnNight = schedulerView.findViewById(R.id.btnNight);
        schedulerButtons.add(btnRecent1);
        schedulerButtons.add(btnRecent2);
        schedulerButtons.add(btnRecent3);
        schedulerButtons.add(btnRecent4);
        schedulerButtons.add(btnMonday);
        schedulerButtons.add(btnTuesday);
        schedulerButtons.add(btnWednesday);
        schedulerButtons.add(btnThursday);
        schedulerButtons.add(btnFriday);
        schedulerButtons.add(btnSaturday);
        schedulerButtons.add(btnSunday);
        schedulerButtons.add(btnMorning);
        schedulerButtons.add(btnAfternoon);
        schedulerButtons.add(btnEvening);
        schedulerButtons.add(btnNight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

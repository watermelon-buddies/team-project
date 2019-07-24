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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.parse.Parse.getApplicationContext;

public class BucketListCurrentFragment extends Fragment {

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
    ImageView blur;
    public View popupView;
    boolean mFirstLoad;

    /* Inflate bucket_list_fragment.xml and bind views using Butterknife */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.bucket_list_current_fragment, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull final View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        /* addItem floating action button for adding new item to the bucket list */
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = inflater.inflate(R.layout.bucket_list_add_popup_window, null);
                Spinner spinnerCategory = popupView.findViewById(R.id.spinnerCategory);
                final DatePicker dpCalendar = popupView.findViewById(R.id.dpCalendar);
                final Button btnBuckIt = popupView.findViewById(R.id.btnBuckIt);
                final Button btnGetDate = popupView.findViewById(R.id.btnGetDate);
                final EditText etDate = popupView.findViewById(R.id.etDate);
                etDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Pop up", "Calendar just popped up");
                        btnGetDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("Pop up", "Set date"+dpCalendar.getYear());
                                btnBuckIt.setVisibility(View.VISIBLE);
                                etDate.setText((dpCalendar.getMonth()+1)+"/"+ (dpCalendar.getDayOfMonth())+"/"+dpCalendar.getYear());
                            }
                        });
                    }
                });
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
                popupWindow.showAtLocation(bucketListView, Gravity.CENTER, 0, 0);
                btnBuckIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        removeBlur();
                    }
                });

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

    private void addBlur() {
        blur.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        blur.startAnimation(aniFade);
    }

    private void removeBlur(){
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        blur.startAnimation(aniFade);
        blur.setVisibility(View.INVISIBLE);
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

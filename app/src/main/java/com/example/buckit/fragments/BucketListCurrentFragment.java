package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.example.buckit.R;
import com.example.buckit.adapters.BucketListAdapter;
import com.example.buckit.models.Bucketlist;
import com.example.buckit.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.buckit.fragments.EventsExploreFragment.API_BASE_URL;
import static com.example.buckit.fragments.EventsExploreFragment.API_KEY_PARAM;
import static com.example.buckit.fragments.EventsExploreFragment.KEY_SELECTED_CATEGORIES;
import static com.example.buckit.fragments.EventsExploreFragment.PRIVATE_TOKEN;
import static com.parse.Parse.getApplicationContext;

public class BucketListCurrentFragment extends Fragment {

    /*
    Fragment activated when bucket icon on bottom naivation bar is clicked that allows user to see
    their bucket list ideas, add ideas with categories specified and go to scheduler to book
    specific time
     */

    @BindView(R.id.rvBucketList)  RecyclerView rvBucketList;
    @BindView(R.id.addItemFab)  FloatingActionButton addItemFab;
    @BindView(R.id.swipeContainer)  SwipeRefreshLayout bucketRefreshSwipeContainer;
    @BindView(R.id.ivBlurBucket) ImageView ivBlurBucket;
    private Unbinder unbinder;
    ArrayList<Bucketlist> mBucketList;
    BucketListAdapter mBucketAdapter;
    public View popupView;
    ParseACL acl;
    boolean mFirstLoad;

    /* Inflate bucket_list_fragment.xml and bind views using Butterknife */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.bucket_list_current, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull final View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        acl = new ParseACL(currentUser);
        acl.setPublicReadAccess(true);
        currentUser.setACL(acl);
        JSONArray categories = currentUser.getJSONArray(KEY_SELECTED_CATEGORIES);
        if (categories == null){
            ArrayList<String> catEmptyList = new ArrayList<>();
            currentUser.put(KEY_SELECTED_CATEGORIES, catEmptyList);
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

        /* addItem floating action button for adding new item to the bucket list */
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlur();
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                popupView = inflater.inflate(R.layout.bucket_list_add_popup_window, null);
                final Spinner spinnerCategory = popupView.findViewById(R.id.spinnerCategory);
                final EditText etItemDescription = popupView.findViewById(R.id.etItemDescription);
                final DatePicker dpCalendar = popupView.findViewById(R.id.dpCalendar);
                final Button btnBuckIt = popupView.findViewById(R.id.btnBuckIt);
                final String deadline = (dpCalendar.getMonth()+1)+"/"+ (dpCalendar.getDayOfMonth())+"/"+dpCalendar.getYear();
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
                popupWindow.showAtLocation(bucketListView, Gravity.CENTER, 0, 0);
                popupWindow.setFocusable(true);
                popupWindow.update();
                btnBuckIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String description = etItemDescription.getText().toString();
                        final ParseUser user = ParseUser.getCurrentUser();
                        final String category = spinnerCategory.getSelectedItem().toString();
                        try {
                            addItemToBucketList(description, user, deadline, category);
                        } catch (java.text.ParseException e) {
                           e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        popupWindow.dismiss();
                        populateBucket();
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

    private void addItemToBucketList(final String description, final ParseUser user, String deadline, final String category) throws java.text.ParseException, JSONException {
        final Bucketlist newItem = new Bucketlist();
        newItem.setName(description);
        newItem.setUser(user);
        newItem.setAchieved(false);
        Date date = changeDateToParseFormat(deadline);
        newItem.setCategory(category);
        JSONArray categories = user.getJSONArray(KEY_SELECTED_CATEGORIES);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url = API_BASE_URL + "categories/";
        params.put(API_KEY_PARAM, PRIVATE_TOKEN);
        boolean exists = false;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");
                    for (int i = 0; i < categories.length(); i++){
                        JSONObject catObject = categories.getJSONObject(i);
                        Log.d("ID", catObject.toString());
                        Log.d("ID", catObject.getString("name"));
                        Log.d("ID", catObject.getString("id"));
                        Log.d("ID", category);
                       if (catObject.getString("name").equals(category) ){
                            Log.d("ID", catObject.getString("id"));
                            user.add(KEY_SELECTED_CATEGORIES, catObject.getString("id"));
                            user.saveInBackground(new SaveCallback() {
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
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        newItem.setDeadline(date);
        Toast.makeText(getContext(), "Added item to bucket list", Toast.LENGTH_LONG);
        /* TODO - Change to snackbar compared to toast */
        newItem.saveInBackground(new SaveCallback() {
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

    private Date changeDateToParseFormat(String strDate) throws java.text.ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date d = (Date)formatter.parse(strDate);
        DateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String parseDate = fmtOut.format(d);
        return fmtOut.parse(parseDate);
    }

    private void addBlur() {
        ivBlurBucket.setVisibility(View.VISIBLE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        ivBlurBucket.startAnimation(aniFade);
    }

    private void removeBlur(){
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        ivBlurBucket.startAnimation(aniFade);
        ivBlurBucket.setVisibility(View.INVISIBLE);
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
        final Bucketlist.Query bucketQuery = new Bucketlist.Query();
        bucketQuery.getTop().withUser();
        bucketQuery.orderByDescending("createdAt");
        bucketQuery.whereEqualTo("achieved", false);

        bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
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

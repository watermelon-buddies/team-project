package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.buckit.R;
import com.example.buckit.adapters.BucketListAdapter;
import com.example.buckit.models.Bucketlist;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.FindCallback;
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
import java.util.HashMap;
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
import static com.example.buckit.models.Bucketlist.KEY_USER;

public class BucketListCurrentFragment extends Fragment {

    /*
    Fragment activated when bucket icon on bottom naivation bar is clicked that allows user to see
    their bucket list ideas, add ideas with categories specified and go to scheduler to book
    specific time
     */

    @BindView(R.id.rvBucketList)  RecyclerView rvBucketList;
    @BindView(R.id.addItemFab)  FloatingActionButton addItemFab;
    @BindView(R.id.ivBlurBucket) ImageView ivBlurBucket;
    @BindView(R.id.tvNoCurrentItems)
    TextView tvNoCurrentItems;
    private Unbinder unbinder;
    ArrayList<Bucketlist> mBucketList;
    BucketListAdapter mBucketAdapter;
    public View popupView;
    boolean mFirstLoad;
    ParseUser user;
    View mView;
    FrameLayout layout_MainMenu;
    HashMap<String, Integer> userEvents;

    /* Inflate bucket_list_fragment.xml and bind views using Butterknife */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bucketListView = inflater.inflate(R.layout.bucket_list_current, container, false);
        unbinder = ButterKnife.bind(this, bucketListView);
        if (getArguments() != null) {
            userEvents = (HashMap<String, Integer>) getArguments().getSerializable("userEvents");
        }
        return bucketListView;
    }

    @Override
    public void onViewCreated(@NonNull final View bucketListView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(bucketListView, savedInstanceState);
        /* addItem floating action button for adding new item to the bucket list */
        user = ParseUser.getCurrentUser();
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowForBucketList(bucketListView);
            }
        });

        mBucketList = new ArrayList<>();
        mBucketAdapter = new BucketListAdapter(getContext(), mBucketList, true, userEvents);
        rvBucketList.setAdapter(mBucketAdapter);
        // associate the LinearLayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBucketList.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvBucketList);
        populateBucket();
        mView = bucketListView;
        layout_MainMenu = (FrameLayout) mView.findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);
    }

    private void addItemToBucketList(final String description, final ParseUser user, String deadline, final String category) throws java.text.ParseException, JSONException {
        final Bucketlist newItem = new Bucketlist();
        newItem.setName(description);
        newItem.setUser(user);
        newItem.setAchieved(false);
        Date date = changeDateToParseFormat(deadline);
        newItem.setCategory(category);
        saveCategoryKeyInUserDatabase(category);
        newItem.setDeadline(date);
        newItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Snackbar.make(popupView, "Successfully added to bucket list!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Log.d("Bucketlist Fragment", "Failed in adding new item to Parse!");
                    e.printStackTrace();
                }
            }
        });

    }

    private void saveCategoryKeyInUserDatabase(final String category) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url = API_BASE_URL + "categories/";
        params.put(API_KEY_PARAM, PRIVATE_TOKEN);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray categories = response.getJSONArray("categories");
                    for (int i = 0; i < response.length(); i++){
                        JSONObject catObject = categories.getJSONObject(i);
                        if (catObject.getString("name").equals(category) ){
                            Log.d("ID", catObject.getString("id"));
                            user.add(KEY_SELECTED_CATEGORIES, catObject.getString("id"));
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("Bucketlist Fragment", "Success in adding new item to User");
                                    } else {
                                        Log.d("Bucketlist Fragment", "Failed in adding new item to User!");
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

    }

    private Date changeDateToParseFormat(String strDate) throws java.text.ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date d = (Date)formatter.parse(strDate);
        DateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String parseDate = fmtOut.format(d);
        return fmtOut.parse(parseDate);
    }

    /* Unbind butterknife binded views when fragment is closed*/
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void popupWindowForBucketList(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        popupView = inflater.inflate(R.layout.bucket_list_add_popup_window, null);
        final Spinner spinnerCategory = popupView.findViewById(R.id.spinnerCategory);
        final EditText etItemDescription = popupView.findViewById(R.id.etItemDescription);
        final DatePicker dpCalendar = popupView.findViewById(R.id.dpCalendar);
        Date date = new Date();
        dpCalendar.setMinDate(date.getTime());
        final Button btnBuckIt = popupView.findViewById(R.id.btnBuckIt);
        final String deadline = (dpCalendar.getMonth()+1)+"/"+ (dpCalendar.getDayOfMonth())+"/"+dpCalendar.getYear();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        layout_MainMenu.getForeground().setAlpha(100);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 40);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layout_MainMenu.getForeground().setAlpha(0);

            }
        });
        btnBuckIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = etItemDescription.getText().toString();
                final String category = spinnerCategory.getSelectedItem().toString();
                //layout_MainMenu.getForeground().setAlpha( 180);
                if (description.length() <= 0) {
                    Snackbar.make(popupView, "Please make sure you filled all required fields", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    try {
                        addItemToBucketList(description, user, deadline, category);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    layout_MainMenu.getForeground().setAlpha(0);
                    popupWindow.dismiss();
                    populateBucket();
                }
            }
        });
    }


    protected void populateBucket() {
        final Bucketlist.Query bucketQuery = new Bucketlist.Query();
        bucketQuery.getTop().withUser();
        bucketQuery.whereEqualTo(KEY_USER, user);
        bucketQuery.orderByDescending("createdAt");
        bucketQuery.whereEqualTo("achieved", false);

        bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
            @Override
            public void done(List<Bucketlist> object, ParseException e) {
                if (object != null && object.size()>0){
                    if (e == null) {
                        mBucketList.clear();
                        mBucketList.addAll(object);
                        mBucketAdapter.notifyDataSetChanged();
                        if (mFirstLoad) {
                            mFirstLoad = false;
                        }

                        Log.d("Timeline Activity", "Successfully loaded posts!");
                    } else {
                        e.printStackTrace();
                    }
                }
                else tvNoCurrentItems.setVisibility(View.VISIBLE);
            }
        });
    }

}

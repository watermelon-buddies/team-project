package com.example.buckit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckit.R;
import com.example.buckit.fragments.BucketListCurrentFragment;
import com.example.buckit.models.Bucketlist;
import com.example.buckit.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;


import org.json.JSONException;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.buckit.models.Bucketlist.KEY_ACHIEVED;
import static com.example.buckit.models.User.KEY_FRIENDS;

public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Bucketlist item);
    }

    private List<Bucketlist> mBucketList;
    private Context mContext;
    public Boolean show;

    public BucketListAdapter(Context context, List<Bucketlist> items, boolean notAchieved) {
        mBucketList = items;
        mContext = context;
        show = notAchieved;
    }

    private boolean click;
    Bucketlist bucketList;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.bucket_list_item_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get data according to position

        bucketList = mBucketList.get(position);
        holder.btnAchieve.setTag(bucketList);
        holder.btnDeleteItem.setTag(bucketList);
        holder.tvBucketDescription.setText(bucketList.getName());
        int id = mContext.getResources().getIdentifier("com.example.buckit:drawable/cat_" + bucketList.getCategory().toLowerCase().substring(0, 4), null, null);
        Log.d("category", String.valueOf(id));
        Glide.with(mContext)
                .load(id)
                .fitCenter()
                .bitmapTransform(new RoundedCornersTransformation(mContext, 20, 0))
                .into(holder.ivCategoryImage);
    }


    @Override
    public int getItemCount() {
        return mBucketList.size();
    }
    // for each row, inflate the layout and cache references intop view

    public void clear() {
        mBucketList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Bucketlist> list) {
        mBucketList.addAll(list);
        notifyDataSetChanged();
    }

    // create the viewholder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvBucketDescription;
        public ImageView ivCategoryImage;
        public ImageView btnBookItem;
        public Button btnAchieve;
        public FloatingActionButton btnEditItem;
        public ImageButton btnDeleteItem;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvBucketDescription = itemView.findViewById(R.id.tvBucketDescription);
            btnBookItem = itemView.findViewById(R.id.btnBookItem);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            btnAchieve = itemView.findViewById(R.id.btnAchievedItem);
            btnAchieve.setOnClickListener(this);
            btnDeleteItem.setOnClickListener(this);
            if (show = true){
                btnAchieve.setVisibility(View.VISIBLE);
                btnBookItem.setVisibility(View.VISIBLE);
            }
            else {
                btnAchieve.setVisibility(View.GONE);
                btnBookItem.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == btnAchieve.getId()){
                Log.d("Click", "Working!");
                Bucketlist item = (Bucketlist) btnAchieve.getTag();
                if (item != null) {
                    Log.d("Click", "Working!");
                    Bucketlist.Query bucketQuery = new Bucketlist.Query();
                    bucketQuery.whereEqualTo("objectId", item.getObjectId());
                    bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
                        @Override
                        public void done(List<Bucketlist> objects, ParseException e) {
                            Bucketlist selectedItem = objects.get(0);
                            selectedItem.put(KEY_ACHIEVED, true);
                            selectedItem.saveInBackground();
                        }
                    });
                    mBucketList.remove(item);
                    notifyDataSetChanged();
                }
            }
            else if(v.getId() == btnDeleteItem.getId()) {
                Bucketlist item = (Bucketlist) btnDeleteItem.getTag();
                if (item != null) {
                    Log.d("Click", "Working!");
                    Bucketlist.Query bucketQuery = new Bucketlist.Query();
                    bucketQuery.whereEqualTo("objectId", item.getObjectId());
                    bucketQuery.findInBackground(new FindCallback<Bucketlist>() {
                        @Override
                        public void done(List<Bucketlist> objects, ParseException e) {
                            Bucketlist selectedItem = objects.get(0);
                            selectedItem.deleteInBackground();
                        }
                    });
                    mBucketList.remove(item);
                    notifyDataSetChanged();
                }
            }
        }
    }

}




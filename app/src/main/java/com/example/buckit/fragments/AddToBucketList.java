package com.example.buckit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckit.R;
import com.example.buckit.models.Bucketlist;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddToBucketList extends DialogFragment {

        EditText etDescription;
        Button btnAdd;
        TextView tvCharCount;


    public AddToBucketList() {

            }

    public static AddToBucketList newInstance(String title) {
            AddToBucketList frag = new AddToBucketList();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            return inflater.inflate(R.layout.bucket_list_add_fragment, container);
            }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDescription = (EditText) view.findViewById(R.id.etDescription);

        btnAdd = view.findViewById(R.id.btnAdd);
        tvCharCount = view.findViewById(R.id.tvCharCount);

        etDescription.addTextChangedListener(mTextEditorWatcher);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = etDescription.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                createPost(description, user);
            }
        });
        String title = getArguments().getString("title", "Compose Tweet");
        getDialog().setTitle(title);
    }

    private void createPost(final String description, ParseUser user) {
        final Bucketlist newItem = new Bucketlist();
        newItem.setName(description);
        newItem.setUser(user);
        Toast.makeText(getContext(), "Shared post", Toast.LENGTH_LONG);
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
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvCharCount.setText(String.valueOf(280-(s.length()))+" characters remaining");
        }

        public void afterTextChanged(Editable s) {
        }
    };


}

package com.example.buckit.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
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
            // Empty constructor is required for DialogFragment
            // Make sure not to add arguments to the constructor
            // Use `newInstance` instead as shown below
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

    public interface EditNameDialogListener {
        void onFinishEditDialog(String text);
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
/*                final File file = new File(imagePath);
                final ParseFile parseFile = new ParseFile(file);*/
                createPost(description, user);
            }
        });
        // Get field from view
        // Fetch arguments from bundle and set title
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
            //This sets a textview to the current length
            tvCharCount.setText(String.valueOf(280-(s.length()))+" characters remaining");
        }

        public void afterTextChanged(Editable s) {
        }
    };


}

package com.example.buckit.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.buckit.R;
import com.example.buckit.models.User;
import com.example.buckit.utils.MultiSelectSpinner;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.buckit.fragments.EventsExploreFragment.KEY_SELECTED_CATEGORIES;
import static com.example.buckit.models.User.KEY_EVENT_RADIUS;

public class SignUpDetailsActivtiy extends AppCompatActivity {

    @BindView(R.id.spinCategoriesSignUp)
    MultiSelectSpinner spinCategoriesSignUp;
    @BindView(R.id.etEventRadius)
    EditText etEventRadius;
    @BindView(R.id.spinDistanceType)
    Spinner spinDistanceType;
    @BindView(R.id.btnFinishRegistration)
    Button btnFinishRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_details_activtiy);
        ButterKnife.bind(this);
        spinCategoriesSignUp.setItems(getResources().getStringArray(R.array.category_names));

        btnFinishRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                List<String> selected = spinCategoriesSignUp.getSelectedStrings();
                String eventRadius = (etEventRadius.getText().toString())+(spinDistanceType.getSelectedItem().toString());
                if (eventRadius == null || selected == null){
                    Toast.makeText(getApplicationContext(), "Please fill in all required inputs", Toast.LENGTH_SHORT);
                }
                else {
                    completeRegistration(user, selected, eventRadius);
                }
            }
        });

    }

    private void completeRegistration(ParseUser user, List<String> categories, String radius){
        user.put(KEY_EVENT_RADIUS, radius);
        ParseACL acl = new ParseACL(user);
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        user.setACL(acl);
        ArrayList<String> catList = new ArrayList<>();
        catList.addAll(categories);
        user.put(KEY_SELECTED_CATEGORIES, catList);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                startActivity(new Intent(SignUpDetailsActivtiy.this, HomeActivity.class));
                Toast.makeText(getApplicationContext(), "Account details saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

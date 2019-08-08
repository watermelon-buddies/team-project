package com.example.buckit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckit.models.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.buckit.models.User.KEY_USERNAME;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnRegister) Button btnRegister;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.bind(this);
        User user = (User) ParseObject.create("_User");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username == null || password == null){
                    Toast.makeText(getApplicationContext(), "Please fill in all required inputs", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerNewUser(username, password);
                }
            }
        });
    }

    private void registerNewUser(final String username, final String password) {
        // Create the ParseUser
        // Set core properties
/*        user.put(KEY_USERNAME, username);
*//*        user.setDeviceId(FirebaseInstanceId.getInstance().getToken());*//*
        user.put("password", password);
       // Invoke signUpInBackground
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e== null) {
                    Toast.makeText(getApplicationContext(), "Account created successfully",
                            Toast.LENGTH_SHORT).show();
                    Log.d("Sign up", "Success");
                }
            }
        });
*//*        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
*//**//*                    Intent i = new Intent();
                    i.putExtra("username", username);
                    i.putExtra("password", password);
                    Log.d("username", username);
                    setResult(RESULT_OK, i);*//**//*
                    Toast.makeText(getApplicationContext(), "Account created successfully",
                            Toast.LENGTH_SHORT).show();
                    Log.d("Sign up", "Success");
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });*/

    }
}

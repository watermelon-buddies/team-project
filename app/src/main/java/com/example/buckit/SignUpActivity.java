package com.example.buckit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckit.activities.SignUpDetailsActivtiy;
import com.example.buckit.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnRegister) Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        ButterKnife.bind(this);

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
        User user = new User();
        // Set core properties
        user.setUsername(username);
        //user.setDeviceId(FirebaseInstanceId.getInstance().getToken());
        user.setPassword(password);
       // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    startActivity(new Intent( SignUpActivity.this, SignUpDetailsActivtiy.class));
                    Toast.makeText(getApplicationContext(), "Account created successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}

package com.example.buckit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckit.activities.HomeActivity;
import com.example.buckit.activities.SignUpDetailsActivtiy;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.btnSignUp) Button btnSignUp;
    public static final int SIGNUP_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser != null) {
                goToHome();


            } else {
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(i);
                }
            });
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Log in", "Click working");
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    login(username, password);
                }
            });
        }

    }

    private void goToHome() {
        final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        // Finish so user can't go back to home screen
        finish();
    }

    // Checks that the user exists and the password corresponds to it
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null){
                    Log.d("LoginActivity", "Login Successful");
                    goToHome();
                } else {
                    Log.e("LoginActivity", "Error");
                    Toast.makeText(getApplicationContext(), "Please check if username and password is correct!", Toast.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
    }

}

package com.example.buckit.utils;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buckit.LoginActivity;
import com.example.buckit.R;

public class Splashscreen extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shrink);
        anim.reset();
        final ImageView circle = findViewById(R.id.splash);
        circle.animate().scaleXBy(3).scaleYBy(3).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                circle.setVisibility(View.GONE);
                setContentView(R.layout.activity_splash_screen_2);
                ImageView ivSplashCircle = findViewById(R.id.ivSplashCircle);
                ImageView ivMainLogo = findViewById(R.id.ivMainLogo);
                TextView tvMainLogo = findViewById(R.id.tvMainLogo);
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                ivSplashCircle.startAnimation(anim);
                tvMainLogo.startAnimation(anim);
                ivMainLogo.startAnimation(anim);
            }
        });
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 1500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(Splashscreen.this,
                            LoginActivity.class);
                    startActivity(intent);
                    Splashscreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    Splashscreen.this.finish();
                }

            }
        };
        splashTread.start();
    }
}
package com.deji_cranium.unilorinleague;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private ImageView mUnilorinLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        final TextView mCredit = (TextView) findViewById(R.id.deji_atoyeb);

        final ProgressBar mProgressbar = (ProgressBar)findViewById(R.id.progressBar);
        mCredit.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_fro_right));
        mProgressbar.setVisibility(View.VISIBLE);
        getSupportActionBar().hide();


        mUnilorinLogo = (ImageView)findViewById(R.id.unilorin_logo);
        mUnilorinLogo.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_fro_left));


        int SPLASH_TIME_OUT = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(SplashScreen.this, Home.class);
                startActivity(in);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
};

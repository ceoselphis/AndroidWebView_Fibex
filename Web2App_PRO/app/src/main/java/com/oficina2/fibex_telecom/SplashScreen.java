package com.oficina2.fibex_telecom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SplashScreen extends AppCompatActivity {

    Animation Splash_top,Splash_bottom;
    TextView splash_name;
    CardView cardView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        cardView = findViewById(R.id.cardView);
        splash_name = findViewById(R.id.splash_name);

        Splash_top = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Splash_bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        cardView.setAnimation(Splash_top);
        splash_name.setAnimation(Splash_bottom);


        //For Splash Screen
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Code here

                Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(myIntent);
                finish();

            }
        },2000);


    } // OnCreate Method End Here =================


} // Public Class End Here ========================
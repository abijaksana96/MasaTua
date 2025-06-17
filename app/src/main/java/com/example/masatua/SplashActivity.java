package com.example.masatua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        new Handler().postDelayed(() -> {
            // Cek apakah ini pertama kali aplikasi dibuka
            SharedPreferences prefs = getSharedPreferences("appPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("firstTime", true);

            if (isFirstTime) {

                prefs.edit().putBoolean("firstTime", false).apply();


                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            } else {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }


        }, SPLASH_DURATION);
    }
}

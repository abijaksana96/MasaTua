package com.example.masatua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper; // Penting: Gunakan Looper.getMainLooper() untuk Handler

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // Durasi splash screen dalam milidetik (2 detik)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh); // PERBAIKI: activity_splash

        // Pindahkan semua logika navigasi ke dalam handler postDelayed
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Cek apakah ini pertama kali aplikasi dibuka
            SharedPreferences prefs = getSharedPreferences("appPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("firstTime", true);

            Intent nextActivityIntent;

            if (isFirstTime) {
                // Jika pertama kali, set flag isFirstTime menjadi false
                prefs.edit().putBoolean("firstTime", false).apply();
                nextActivityIntent = new Intent(SplashActivity.this, IntroActivity.class);
            } else {
                // Jika bukan pertama kali, cek status login Firebase
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    nextActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Jika belum login atau email belum terverifikasi, tetap ke IntroActivity
                    nextActivityIntent = new Intent(SplashActivity.this, IntroActivity.class);
                }
            }

            // Mulai aktivitas berikutnya
            startActivity(nextActivityIntent);
            // Selesaikan SplashActivity agar pengguna tidak bisa kembali ke sini dengan tombol back
            finish();

        }, SPLASH_DURATION);
    }
}
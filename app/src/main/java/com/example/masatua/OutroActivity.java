package com.example.masatua;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class OutroActivity extends AppCompatActivity {
    // SECTION - AHMAD RAFLY
    // region UI
    private ImageView introImage;
    private TextView titleText, descText;
    private LinearLayout dotIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outro);

        introImage = findViewById(R.id.introImage);
        titleText = findViewById(R.id.titleText);
        descText = findViewById(R.id.descText);
        dotIndicator = findViewById(R.id.dotIndicator);
    }
    // endregion UI
}


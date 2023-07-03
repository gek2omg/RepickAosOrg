package com.trendpicker.repick;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        new Handler().postDelayed(() -> {
            Intent inte = new Intent(SplashActivity.this , MainActivity.class);
            if (getIntent().getExtras() != null) {
                inte.putExtras(getIntent().getExtras());
            }
            startActivity(inte);
            finish();
        },1500); // 1500
    }
}
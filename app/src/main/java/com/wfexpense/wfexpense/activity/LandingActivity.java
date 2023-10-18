package com.wfexpense.wfexpense.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wfexpense.wfexpense.R;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Handler handler = new Handler();
        handler.postDelayed(()->{
            startActivity(new Intent(LandingActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.right_fragment_animation, R.anim.exit_right_to_left);
            finish();
        },2000);
    }
}
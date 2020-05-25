package com.example.final_juego;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

public class LoadActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                boolean session = preferences.getBoolean("session", false);
                if(session){
                    Intent menuScreen = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(menuScreen);
                    finish();
                }else{
                    Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginScreen);
                    finish();
                }
            }
        }, 2000);
    }
}

package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StatisticsActivity extends AppCompatActivity {

    ImageView moveBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        moveBack = findViewById(R.id.statMoveBack);

        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveBackIntent = new Intent(StatisticsActivity.this,MainActivity.class);
                finish();
            }
        });

    }
}
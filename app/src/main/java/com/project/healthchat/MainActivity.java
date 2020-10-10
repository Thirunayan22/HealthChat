package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button findHospitalBtn;
    Button statisticsBtn;
    Button chatbotsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findHospitalBtn = findViewById(R.id.findHospitalBtn);
        statisticsBtn   = findViewById(R.id.statistics);
        chatbotsBtn     =  findViewById(R.id.chatbot);


        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statIntent = new Intent(MainActivity.this,StatisticsActivity.class);
                startActivity(statIntent);
            }
        });

        findHospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri hospitalIntentUri = Uri.parse("geo:0,0?z=18&q=hospitals near me");
                Intent hospitalIntent  = new Intent(Intent.ACTION_VIEW, hospitalIntentUri);
                hospitalIntent.setPackage("com.google.android.apps.maps");
                startActivity(hospitalIntent);
            }
        });

    }
}
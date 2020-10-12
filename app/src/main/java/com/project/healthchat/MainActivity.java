package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    Button findHospitalBtn;
    Button statisticsBtn;
    Button chatbotsBtn;
    View parentView;
    private BroadcastReceiver receiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findHospitalBtn = findViewById(R.id.findHospitalBtn);
        statisticsBtn   = findViewById(R.id.statistics);
        chatbotsBtn     =  findViewById(R.id.chatbot);
        parentView      = findViewById(R.id.mainActivityLayout);

        receiver        = new Receiver(parentView);
        broadcastIntent();

        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statIntent = new Intent(MainActivity.this,StatisticsActivity.class);
                startActivity(statIntent);
            }
        });

        chatbotsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent  = new Intent(MainActivity.this,chatBot.class);
                startActivity(chatIntent);
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

    public void broadcastIntent(){
        registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
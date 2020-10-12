package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.sql.Array;
import java.text.DecimalFormat;

public class StatisticsActivity extends AppCompatActivity {

    private static final int WHITE =  000;
    private static final int BLACK =  111;
    private ImageView moveBack;

    private TextView infectedTV;
    private TextView deceasedTV;
    private TextView recoveredTV;

    private Button localBtn;
    private Button globalBtn;

    View parentView;

    private BroadcastReceiver receiver = null;
    boolean isConnected = true;
    private boolean monitoringActivity = false;
    String url ="https://hpb.health.gov.lk/api/get-current-statistical";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Context context = this;


        moveBack = findViewById(R.id.statMoveBack);
        infectedTV = findViewById(R.id.totalInfectedCount);
        deceasedTV  = findViewById(R.id.totalDeathCount);
        recoveredTV = findViewById(R.id.totalRecovered);

        localBtn = findViewById(R.id.localBtn);
        globalBtn = findViewById(R.id.globalBtn);

        parentView = findViewById(R.id.statisticsActivityLayout);

        receiver = new Receiver(parentView);
        broadcastIntent();


        JsonObjectRequest objectRequest = makeSlHealthApiRequest(infectedTV,deceasedTV,recoveredTV,"local");

        localBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localBtn.setTextColor(getResources().getColor(R.color.white));
                localBtn.setBackgroundResource(R.drawable.selected_area_button);

                globalBtn.setTextColor(getResources().getColor(R.color.black));
                globalBtn.setBackgroundResource(R.drawable.non_selected_area_background);

                JsonObjectRequest localObjectRequest = makeSlHealthApiRequest(infectedTV,deceasedTV,recoveredTV,"local");
            }
        });

        globalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalBtn.setTextColor(getResources().getColor(R.color.white));
                globalBtn.setBackgroundResource(R.drawable.selected_area_button);

                localBtn.setTextColor(getResources().getColor(R.color.black));
                localBtn.setBackgroundResource(R.drawable.non_selected_area_background);

                JsonObjectRequest globalObjectRequest   = makeSlHealthApiRequest(infectedTV,deceasedTV,recoveredTV,"global");

            }
        });




        moveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveBackIntent = new Intent(StatisticsActivity.this,MainActivity.class);
                startActivity(moveBackIntent);
              finish();
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

    private JsonObjectRequest  makeSlHealthApiRequest(final TextView totalInfected , final TextView totalDeath , final TextView totalRecovered, final String region){
        /*
        PARAM :
        totalInfected : TextView containing total infected count
        totalDeath  : TextView containing total death count
        totalRecovered : TextView containing total recovered count
        region  : "local" or "global" depending on needed results
              */
        final int million = 1000000;
        final int hundredThousand  = 100000;
        final DecimalFormat formatter = new DecimalFormat("#,###,###");

        final JSONObject[] finalResponse = new JSONObject[1];
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");

                    int totalLocalDeaths = data.getInt("local_deaths");

                    int totalLocalRecoveries = data.getInt("local_recovered");
                    int totalLocalActiveCases = data.getInt("local_active_cases");

                    int totalGlobalDeaths = data.getInt("global_deaths");
                    int totalGlobalRecoveries = data.getInt("global_recovered");
                    int totalGlobalActiveCases = data.getInt("global_total_cases");

                    if(region == "local") {


                        if((totalLocalDeaths > million)){
                            String locDeathFraction = calculateFraction(totalLocalDeaths,million) + "M";
                            totalDeath.setText(locDeathFraction);

                        }else{
                            String locDeath = formatter.format(totalLocalDeaths);
                            totalDeath.setText(locDeath);
                        }

                        if((totalLocalActiveCases > million)){
                            String locActiveCasesFraction = calculateFraction(totalLocalActiveCases,million)+ "M";
                            totalInfected.setText(locActiveCasesFraction);

                        }else{

                            String locActiveCases = formatter.format(totalLocalActiveCases);
                            totalInfected.setText(locActiveCases);

                        }

                        if((totalLocalRecoveries > million) ){
                            String locRecoveriesFraction =  calculateFraction(totalLocalRecoveries,million) + "M";
                            totalRecovered.setText(locRecoveriesFraction);

                        }else{
                            String locRecoveries = formatter.format(totalLocalRecoveries);
                            totalRecovered.setText(locRecoveries);
                        }

                    }
                    else if(region == "global"){

                        if(totalGlobalActiveCases > million ){
                            String globalCasesFraction  = calculateFraction(totalGlobalActiveCases,million) + "M";
                            totalInfected.setText(globalCasesFraction);

                        }else{
                            String globalCases = formatter.format(totalGlobalActiveCases);
                        totalInfected.setText(globalCases);
                        }

                        if(totalGlobalDeaths > million){
                            String globalDeathsFraction = calculateFraction(totalGlobalDeaths,million) + "M";
                            totalDeath.setText(globalDeathsFraction);


                        }else{
                            String globalDeaths = formatter.format(totalGlobalDeaths);
                            totalDeath.setText(globalDeaths);

                        }

                        if(totalGlobalRecoveries > million){
                            String globalRecoveriesFraction = calculateFraction(totalGlobalRecoveries,million)+ "M";
                            totalRecovered.setText(globalRecoveriesFraction);
                        }
                        else{
                            String recoveriesFraction = formatter.format(totalGlobalRecoveries);
                            totalRecovered.setText(recoveriesFraction);

                        }
                    }

                    Log.e("Local Deaths " , String.valueOf(totalLocalDeaths));
                    Log.e("Local recoveries " , String.valueOf(totalLocalRecoveries));
                    Log.e("Local active cases",String.valueOf(totalLocalActiveCases));

                    Log.e("Global Deaths", String.valueOf(totalGlobalDeaths));
                    Log.e("Global Recoveries",String.valueOf(totalGlobalRecoveries));
                    Log.e("Global Active cases ",String.valueOf(totalGlobalActiveCases));
                    finalResponse[0] = response;

                } catch (JSONException e) {
                    Log.e("JSON Error",e.toString());
                    e.printStackTrace();
                }
                Log.e("Rest Response",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response",error.toString());

            }
        });

        requestQueue.add(jsonObjectRequest);
        return jsonObjectRequest;


    }

    private float calculateFraction(long number , long divisor){
        long truncate =  (number * 10L + (divisor/2L))/divisor;
        float fraction  = (float) truncate * 0.10F;
        return fraction;
    }
}
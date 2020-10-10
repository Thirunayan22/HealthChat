package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

    String url ="https://hpb.health.gov.lk/api/get-current-statistical";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        moveBack = findViewById(R.id.statMoveBack);
        infectedTV = findViewById(R.id.totalInfectedCount);
        deceasedTV  = findViewById(R.id.totalDeathCount);
        recoveredTV = findViewById(R.id.totalRecovered);

        localBtn = findViewById(R.id.localBtn);
        globalBtn = findViewById(R.id.globalBtn);


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
                finish();
            }
        });



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
        final DecimalFormat df = new DecimalFormat("0.00");

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

                            String totalLocDeaths = Float.toString(Math.round(totalLocalDeaths/million)) + "M";
                            totalDeath.setText(totalLocDeaths);
                        }else{
                            totalDeath.setText(String.valueOf(totalLocalRecoveries));
                        }

                        if((totalLocalActiveCases > million)){
                            String totalLocActiveCases = Float.toString(Math.round(totalLocalActiveCases/million)) + "M";
                            totalInfected.setText(totalLocActiveCases);

                        }else{
                            totalInfected.setText(String.valueOf(totalLocalActiveCases));

                        }

                        if((totalLocalRecoveries > million) ){
                            String totalLocRecoveries =  Float.toString(Math.round(totalLocalRecoveries/million)) + "M";
                            totalRecovered.setText(totalLocRecoveries);

                        }else{
                            totalRecovered.setText(String.valueOf(totalLocalDeaths));
                        }

                    }
                    else if(region == "global"){

                        if(totalGlobalActiveCases > million ){
                            String totalGlobCases  = Float.toString(Math.round(totalGlobalActiveCases/million)) + "M";
                            totalInfected.setText(totalGlobCases);

                        }else{
                        totalInfected.setText(String.valueOf(totalGlobalActiveCases));
                        }

                        if(totalGlobalDeaths > million){
                            String totalGlobDeaths = Float.toString(Math.round(totalGlobalDeaths/million)) + "M";
                            totalDeath.setText(totalGlobDeaths);

                        }else{
                            totalDeath.setText(String.valueOf(totalGlobalDeaths));
                        }

                        if(totalGlobalRecoveries > million){
                            String totalGlobRecoveries = Float.toString(Math.round(totalGlobalRecoveries/million))+ "M";
                            totalRecovered.setText(totalGlobRecoveries);
                        }
                        else{
                        totalRecovered.setText(String.valueOf(totalGlobalRecoveries));
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
}
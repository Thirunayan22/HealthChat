package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class StatisticsActivity extends AppCompatActivity {

    private static final int WHITE =  000;
    private static final int BLACK =  111;
    private ImageView moveBack;

    private TextView infectedTV;
    private TextView deceasedTV;
    private TextView recoveredTV;
    private ImageView flag;

    HttpRequests httpRequests;

    Spinner spinner;

    View parentView;


    private BroadcastReceiver receiver = null;
    boolean isConnected = true;
    private boolean monitoringActivity = false;
    String url ="https://hpb.health.gov.lk/api/get-current-statistical";
    String internationalUrl = "https://covidapi.info/api/v1/country/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        final Context context = this;


        moveBack = findViewById(R.id.statMoveBack);
        infectedTV = findViewById(R.id.totalInfectedCount);
        deceasedTV  = findViewById(R.id.totalDeathCount);
        recoveredTV = findViewById(R.id.totalRecovered);

        flag = findViewById(R.id.flag);

        spinner = findViewById(R.id.spinner);

        httpRequests = new HttpRequests();

        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.list)));



        parentView = findViewById(R.id.statisticsActivityLayout);

        receiver = new Receiver(parentView);
        broadcastIntent();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String selectedItem = parent.getSelectedItem().toString();
                        flag.setImageResource(CountryData.countryFlag[spinner.getSelectedItemPosition()]);
                        Log.e("Selected item ",selectedItem);


                        if(selectedItem.equals("Global")){
                            Log.e("make request","making request");
                            JsonObjectRequest globalObjectRequest   = httpRequests.makeSlHealthApiRequest(url,infectedTV,deceasedTV,recoveredTV,"global",context);

                        }

                        else if(selectedItem.equals("Sri Lanka")){
                            JsonObjectRequest globalObjectRequest   = httpRequests.makeSlHealthApiRequest(url,infectedTV,deceasedTV,recoveredTV,"local",context);

                        }

                        else{
                            String[] arraylist = getResources().getStringArray(R.array.list);
                            String[] arrayListISO = getResources().getStringArray(R.array.iso_codes);
                            for(int i=0;i<arraylist.length;i++ ){
                                if(arraylist[i].equals(selectedItem)){
                                    Log.e("Found match",arraylist[i]);
                                    Log.e("Index", i+" ");
                                    Log.e("ISO code",arrayListISO[i]);

                                    String ISOCode = arrayListISO[i];
                                    httpRequests.getInternationalCovidData(internationalUrl,ISOCode,infectedTV,deceasedTV,recoveredTV,context);



                                }
                            }

                        }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
        Intent moveBackIntent  = new Intent(this,MainActivity.class);
        startActivity(moveBackIntent);
        finish();
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
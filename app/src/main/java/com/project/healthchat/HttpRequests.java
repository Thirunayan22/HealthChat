package com.project.healthchat;

import android.app.DownloadManager;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.text.format.DateFormat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequests {

    public JsonObjectRequest makeSlHealthApiRequest(String url,final TextView totalInfected , final TextView totalDeath , final TextView totalRecovered, final String region,Context context){
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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



    public float calculateFraction(long number , long divisor){
        long truncate =  (number * 10L + (divisor/2L))/divisor;
        float fraction  = (float) truncate * 0.10F;
        return fraction;
    }

    public void getInternationalCovidData(String url,String ISOcode,final TextView totalInfected , final TextView totalDeath , final TextView totalRecovered, Context context){
        final int million = 1000000;
        final int hundredThousand  = 100000;
        final DecimalFormat formatter = new DecimalFormat("#,###,###");

        final JSONObject[] finalResponse = new JSONObject[1];
        RequestQueue requestQueue = Volley.newRequestQueue(context);


        String requestQuery = url+ISOcode+"/latest";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestQuery, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String date = (String) DateFormat.format("yyyy-MM-dd",new java.util.Date());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date myDate = dateFormat.parse(date);
                    Date newDate = new Date(myDate.getTime() - 172800000L);
                    String finalDate = dateFormat.format(newDate);
                    Log.e("Date",finalDate);
                    JSONObject  covidData = response.getJSONObject("result").getJSONObject(finalDate);
                    int covidInternationalCases = covidData.getInt("confirmed");
                    int covidInternationalDeaths = covidData.getInt("deaths");
                    int covidInternationalRecovered = covidData.getInt("recovered");

                    Log.i("Cases",covidInternationalCases+" ");
                    Log.i("Deaths",covidInternationalDeaths+" ");
                    Log.i("Recovered",covidInternationalRecovered+" ");

                    totalInfected.setText(covidInternationalCases+" ");
                    totalDeath.setText(covidInternationalDeaths + " " );
                    totalRecovered.setText(covidInternationalRecovered+" ");

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                try{
//                    JSONObject data = response.getJSONObject("result").getJSONObject()
//                }
                Log.e("Country response ", response.toString());


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Recieved error",error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);

    }




}

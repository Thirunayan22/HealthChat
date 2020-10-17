package com.project.healthchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private static final int WHITE =  000;
    private static final int BLACK =  111;
    private ImageView moveBack;

    private TextView infectedTV;
    private TextView deceasedTV;
    private TextView recoveredTV;
    private ImageView flag;

    PieChart pieChart;
    BarChart barchart;
    LineChart lineChart;

    HttpRequests httpRequests;
    Spinner spinner;
    View parentView;


    private BroadcastReceiver receiver = null;
    boolean isConnected = true;
    private boolean monitoringActivity = false;
    String url ="https://hpb.health.gov.lk/api/get-current-statistical";
    String internationalUrl = "https://covidapi.info/api/v1/country/";
    String TIMESERIESURL = "https://covidapi.info/api/v1/country/";
    public ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        final Context context = this;


        moveBack = findViewById(R.id.statMoveBack);
        infectedTV = findViewById(R.id.totalInfectedCount);
        deceasedTV  = findViewById(R.id.totalDeathCount);
        recoveredTV = findViewById(R.id.totalRecovered);


        pieChart = findViewById(R.id.statPieChat);
        barchart = findViewById(R.id.statBarChart);
        lineChart = findViewById(R.id.statLineChart);

//        /*  ----------------------------------------Pie chart dummy data code ---------------------------------------------------------------------*/
//        //TODO
//        ArrayList<PieEntry> covidCases = new ArrayList<PieEntry>();
//
//        covidCases.add(new PieEntry(945f,"2008"));
//        covidCases.add(new PieEntry(91345f,"2009"));
//        covidCases.add(new PieEntry(123f,"2010"));
//        covidCases.add(new PieEntry(94235f,"2011"));
//        covidCases.add(new PieEntry(923f,"2012"));
//        covidCases.add(new PieEntry(9453f,"2013"));
//
//
//
//        PieDataSet dataSet = new PieDataSet(covidCases,"Number of cases");
//
//        PieData data  = new PieData(dataSet);
//        data.setDrawValues(false);
//        pieChart.setData(data);
//
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieChart.animateXY(1000, 1000);

        /*  ----------------------------------------Pie chart dummy data code ---------------------------------------------------------------------*/

        /*  ----------------------------------------Bar chart dummy data code ---------------------------------------------------------------------*/

    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
    barEntries.add(new BarEntry(945f, 0));
    barEntries.add(new BarEntry(1040f, 1));
    barEntries.add(new BarEntry(1043f, 2));
    barEntries.add(new BarEntry(1044f, 3));
    barEntries.add(new BarEntry(1069f, 4));
    barEntries.add(new BarEntry(1087f, 5));
    barEntries.add(new BarEntry(1001f, 6));

    ArrayList<String> year = new ArrayList<String>();

    year.add("2008");
    year.add("2009");
    year.add("2010");
    year.add("2011");
    year.add("2012");
    year.add("2013");
    year.add("2014");

    BarDataSet barDataSet = new BarDataSet(barEntries,"Corona Cases");
    barchart.animateY(2000);
    BarData barData = new BarData(barDataSet);
    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
    barchart.setData(barData);

    /*  ----------------------------------------Bar chart dummy data code ---------------------------------------------------------------------*/

        /*  ----------------------------------------Line chart dummy data code ---------------------------------------------------------------------*/

        ArrayList<Entry> pcrTesting  = new ArrayList<Entry>();
        ArrayList<Entry> deaths = new ArrayList<Entry>();

        pcrTesting.add(new Entry(0,100000));
        pcrTesting.add(new Entry(1,140000));
        pcrTesting.add(new Entry(2,143400));
        pcrTesting.add(new Entry(3,243400));

        deaths.add(new Entry(0,1000));
        deaths.add(new Entry(1,13432));
        deaths.add(new Entry(2,34234));
        deaths.add(new Entry(3,23553));

        LineDataSet lineDataSet1 = new LineDataSet(pcrTesting,"PCR Testing");
        LineDataSet lineDataSet2 = new LineDataSet(deaths,"Deaths");

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
        lineDataSets.add(lineDataSet1);
        lineDataSets.add(lineDataSet2);
        LineData lineData  = new LineData(lineDataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.animate();

        /*  ----------------------------------------Line chart dummy data code ---------------------------------------------------------------------*/

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
                            JsonObjectRequest  globalObjectRequest2   = makeSlHealthApiRequest(url,infectedTV,deceasedTV,recoveredTV,"global");
//


                        }

                        else if(selectedItem.equals("Sri Lanka")){

                            JsonObjectRequest globalObjectRequest3   = makeSlHealthApiRequest(url,infectedTV,deceasedTV,recoveredTV,"local");
                            JsonObjectRequest jsonObjectRequestTS = httpRequests.makeTimeSeriesRequest(lineChart,TIMESERIESURL,"LKA","2020-10-1","2020-10-15",context);


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
                                   ArrayList<PieEntry> intRequestResponse =  httpRequests.getInternationalCovidData(pieChart,internationalUrl,ISOCode,infectedTV,deceasedTV,recoveredTV,context);

                                    JsonObjectRequest jsonObjectRequestTS = httpRequests.makeTimeSeriesRequest(lineChart,TIMESERIESURL,ISOCode,"2020-10-1","2020-10-15",context);

                                }
                            }

                        }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                JsonObjectRequest globalObjectRequest   = makeSlHealthApiRequest(url,infectedTV,deceasedTV,recoveredTV,"global");


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

    private JsonObjectRequest  makeSlHealthApiRequest(String url,final TextView totalInfected , final TextView totalDeath , final TextView totalRecovered, final String region){
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
                        ArrayList<PieEntry> covidCasesLocal = new ArrayList<PieEntry>();


                        covidCasesLocal.add(new PieEntry(totalLocalDeaths,"Local Deaths"));
                        covidCasesLocal.add(new PieEntry(totalLocalActiveCases,"Local Active Cases"));
                        covidCasesLocal.add(new PieEntry(totalLocalRecoveries,"Local Recoveries"));

                        PieDataSet pieDataSetLocal= new PieDataSet(covidCasesLocal ,"Covid Progression Global");
                        PieData pieDataLocal = new PieData(pieDataSetLocal);
                        pieDataLocal.setDrawValues(false);
                        pieChart.setData(pieDataLocal);
                        pieChart.setDrawEntryLabels(false);
                        pieChart.setDrawEntryLabels(true);
                        pieChart.setEntryLabelColor(Color.GRAY);


                        pieDataSetLocal.setColors(ColorTemplate.COLORFUL_COLORS);
                        pieChart.animateXY(1000,1000);


                        Log.e("Local Value",covidCasesLocal.toString());


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
                        Log.e("Moved : " , "into global");
                        ArrayList<PieEntry> covidCasesGlobal = new ArrayList<PieEntry>();

                        covidCasesGlobal.add(new PieEntry(totalGlobalDeaths,"Global Deaths"));
                        covidCasesGlobal.add(new PieEntry(totalGlobalActiveCases,"Global Active Cases"));
                        covidCasesGlobal.add(new PieEntry(totalGlobalRecoveries,"Global Recoveries"));
                        pieChart.setDrawEntryLabels(true);
                        pieChart.setEntryLabelColor(Color.GRAY);


                        PieDataSet pieDataSetGlobal = new PieDataSet(covidCasesGlobal ,"");
                        PieData pieDataLocal = new PieData(pieDataSetGlobal);
                        pieDataLocal.setDrawValues(false);

                        pieChart.setData(pieDataLocal);

                        Legend legend = pieChart.getLegend();
                        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        legend.setEnabled(true);
                        pieDataSetGlobal.setColors(ColorTemplate.COLORFUL_COLORS);
                        pieChart.animateXY(1000,1000);

                        Log.e("Covid Global",covidCasesGlobal.toString());

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
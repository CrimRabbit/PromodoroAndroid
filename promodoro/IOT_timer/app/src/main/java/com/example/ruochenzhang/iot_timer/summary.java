package com.example.ruochenzhang.iot_timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class summary extends AppCompatActivity {
    private List<String> durations ;
    private List<String> states;
    private List<PieEntry> entries ;
    private float total_time= 4*1000*60;
    private float working_time = 20*1000;
    private float coffee_time = 40*1000;
    private float break_time = 40*1000;
    private float email_time = 20*1000;
    private float research_time = 60*1000;
    private float meeting_time = 60*1000;
    private String working_created;
    private String working_updated;
    private String coffee_created;
    private String coffee_updated;
    private String research_created;
    private String research_updated;
    SharedPreferences sharedPref;
    private String APIUrl = "http://iotfocus.herokuapp.com/api/";
    private Object mAuthTask = null;
    JsonObjectRequest getRequest = null;
    RequestQueue queue = null;
    String current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        working_created = sharedPref.getString(getString(R.string.startTime),"");
        working_updated = sharedPref.getString(getString(R.string.updateTime),"");
        float difference = timeDifference(working_created,working_updated);
        current_state = sharedPref.getString(getString(R.string.currentState),"");
        if(current_state.equals("Working")){
            setWorking_time(difference);
            setTotal_time(difference);
        }
        else if(current_state.equals("Coffee")){
            setCoffee_time(difference);
            setTotal_time(difference);
        }
        else if(current_state.equals("Research")){
            setResearch_time(difference);
            setTotal_time(difference);
        }
        else if(current_state.equals("Meeting")){
            setMeeting_time(difference);
            setTotal_time(difference);
        }
        else if(current_state.equals("Email")){
            setEmail_time(difference);
            setTotal_time(difference);
        }
        else if(current_state.equals("Break")){
            setBreak_time(difference);
            setTotal_time(difference);
        }else {
        }
//        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
//        entries = new ArrayList<>();
//        DurationNStates();
//        PieDataSet set = new PieDataSet(entries,"Daily Activities");
//        PieData data = new PieData(set);
//        pieChart.setData(data);
//        data.setValueTextSize(20f);
//        set.setColors(ColorTemplate.MATERIAL_COLORS);
//        pieChart.animateY(2000);
//        Legend legends = pieChart.getLegend();
//        legends.setTextSize(12f);
//        legends.setTextColor(Color.WHITE);
//        legends.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
//        legends.setEnabled(false);
//        pieChart.invalidate();

        //piechart
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentage(getMeeting_time(),getTotal_time())*100, "Meeting"));
        entries.add(new PieEntry(percentage(getCoffee_time(),getTotal_time())*100, "Coffee"));
        entries.add(new PieEntry(percentage(getBreak_time(),getTotal_time())*100, "Break"));
        entries.add(new PieEntry(percentage(getWorking_time(),getTotal_time())*100, "Working"));
        entries.add(new PieEntry(percentage(getResearch_time(),getTotal_time())*100,"Research"));
        entries.add(new PieEntry(percentage(getEmail_time(),getTotal_time())*100,"Email"));

        PieDataSet set = new PieDataSet(entries, "Daily Activities");
        PieData data = new PieData(set);
        pieChart.setData(data);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.WHITE);


        set.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.animateY(2000);

        Legend legends = pieChart.getLegend();
        legends.setTextSize(12f);
        legends.setTextColor(Color.WHITE);
        legends.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legends.setEnabled(false);
        pieChart.invalidate();

    }

    public void DurationNStates() {
        // TODO: 12/12/16 pull down durations for different states, add to durations and states
        durations = new ArrayList<>();
        states = new ArrayList<>();
        float percentage;
        for (int i = 0; i < durations.size(); i++) {
            int duration_int = Integer.parseInt(durations.get(i));
            percentage = duration_int / total_time;
            entries.add(new PieEntry(percentage, states.get(i)));
        }
    }

    public float percentage(float state_time, float total_time){
        return state_time/total_time;
    }
    public float getWorking_time(){
        return working_time;
    }
    public float getCoffee_time(){
        return coffee_time;
    }
    public float getMeeting_time(){
        return meeting_time;
    }
    public float getResearch_time(){
        return research_time;
    }
    public float getBreak_time(){
        return break_time;
    }
    public float getTotal_time(){
        return total_time;
    }
    public float getEmail_time(){
        return email_time;
    }
    public void setWorking_time(float working_time) {
        this.working_time += working_time;
    }

    public void setCoffee_time(float coffee_time) {
        this.coffee_time += coffee_time;
    }

    public void setBreak_time(float break_time) {
        this.break_time += break_time;
    }

    public void setEmail_time(float email_time) {
        this.email_time += email_time;
    }

    public void setResearch_time(float research_time) {
        this.research_time += research_time;
    }

    public void setMeeting_time(float meeting_time) {
        this.meeting_time += meeting_time;
    }
    public void setTotal_time(float meeting_time) {
        this.total_time += meeting_time;
    }
    //expects 2x inputs of "2016-12-13T17:17:32.955Z" format
    public static float timeDifference(String created, String updated){
        Pattern p = Pattern.compile("T.*\\.");
        Matcher m = p.matcher(created);
//    String cTrim = "";
        float cNum=0;
        int count=0;
        if (m.find()){
            //cTrim = m.group(0).substring(1,m.group(0).indexOf("."));
//      cTrim = m.group(0).substring(1, m.group(0).length()-1);
//      String[] arrC = cTrim.split(":");
            String[] arrC = m.group(0).substring(1, m.group(0).length()-1).split(":");
            cNum+= Float.parseFloat(arrC[arrC.length-1]);
            for(int i =2;i>0;i--){
                cNum += Float.parseFloat(arrC[count])*Math.pow(60, i);
                count++;
            }
        }
        count=0;
        m = p.matcher(updated);
        float uNum=0;
        if(m.find()){
            String[] arrC = m.group(0).substring(1, m.group(0).length()-1).split(":");
            uNum+= Float.parseFloat(arrC[arrC.length-1]);
            for(int i =2;i>0;i--){
                uNum += Float.parseFloat(arrC[count])*Math.pow(60, i);
                count++;
            }
        }
        return uNum-cNum;
    }

//    public String requestState() {
//        int userId = sharedPref.getInt(getString(R.string.prefsUserId),0);
//        String newURL = APIUrl + "totemdatum/latest_state?id=" + userId;
//        getRequest = new JsonObjectRequest(Request.Method.GET, newURL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//                        //Toast.makeText(getApplication().getBaseContext(), response.get("age").toString(),Toast.LENGTH_SHORT).show();
//                        Log.d("Response", response.toString());
//                        mAuthTask = null;
//                        checkCurentState(response);
////                        Toast.makeText(getApplicationContext(),answer+"",Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error.Response", error.toString());
//                        checkCurentState(null);
//                    }
//                }
//        );
//        queue.add(getRequest);
//        return current_state;

//    }
}

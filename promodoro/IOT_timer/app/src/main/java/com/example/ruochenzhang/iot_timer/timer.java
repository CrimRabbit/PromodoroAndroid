package com.example.ruochenzhang.iot_timer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class timer extends AppCompatActivity {
    LinearLayout dropDownMenuIconItem;
    long workingtime=25*60*1000;
    long restingtime=5*60*1000;
    TextView timer;
    int start_count=0;
    CountDownTimer workingt;
    CountDownTimer restingt;
    int totalworkingtime_day=0;
    ImageButton settingsB;
    ImageButton summaryB;
    SharedPreferences sharedPref;
    final int NOTIFY_ID=100;
    NotificationCompat.Builder mBuilder;
    NotificationManager mgr;
    private String APIUrl = "http://iotfocus.herokuapp.com/api/";
    private Object mAuthTask = null;
    JsonObjectRequest getRequest = null;
    RequestQueue queue =null;
    private int FIVE_SECONDS = 5000;
    final Handler handler = new Handler();
    boolean answer;
    String current_State;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get durations from settings
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);

        queue = Volley.newRequestQueue(this);
//        setRestingtime(sharedPref.getInt("restDuration",5));
//        setWorkingTime(sharedPref.getInt("promodoroDuration",25));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        // TODO: 13/12/16  get status from server every five second, if request state is true, start timer
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 5 seconds
                current_State = sharedPref.getString(getString(R.string.currentState),"");
                boolean tobegin = requestState();
//                Toast.makeText(getApplicationContext(),tobegin+"",Toast.LENGTH_SHORT).show();
                if(tobegin){
                    start2();
                }
                if(!current_State.equals("Working")){
                    workingt.cancel();
                }
                handler.postDelayed(this,FIVE_SECONDS);
            }
        }, FIVE_SECONDS);


        dropDownMenuIconItem = (LinearLayout) findViewById(R.id.vertical_dropdown_icon_menu_items);
        timer = (TextView) findViewById(R.id.timer);
        settingsB = (ImageButton) findViewById(R.id.settingsB);
        summaryB = (ImageButton) findViewById(R.id.summaryB);

        //add notification

        Intent notificationIntent = new Intent(this, Notification.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Focus")
                .setContentText("Time is up!");

        mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        //working time state

        //resting state
//        restingt = new CountDownTimer(restingtime, 1000) {
//            public void onTick(long millisUntilFinished) {
//                long remainedSecs = millisUntilFinished / 1000;
//                if(String.valueOf(remainedSecs%60).length()==1){
//                    timer.setText((remainedSecs / 60) + ":" + "0"+ (remainedSecs % 60));// manage it accordign to you
//                }else {
//                    timer.setText((remainedSecs / 60) + ":" + (remainedSecs % 60));
//                }
//            }
//            public void onFinish() {
//                timer.setText("00:00");
//                restingt.cancel();
//                timer.setText(workingtime/60/1000+":00");
//            }
//        };

    }

    public void setWorkingTime(int time){
        workingtime = time*60*1000;
    }
    public void setRestingtime(int time){
        restingtime = time*60*1000;
    }


    public void start(View v){
        start_count++;
        if (start_count==1){
            setWorkingTime(sharedPref.getInt("promodoroDuration",25));
            workingt = new CountDownTimer(workingtime, 1000) {
                public void onTick(long millisUntilFinished) {
                    long remainedSecs = millisUntilFinished / 1000;
                    if(String.valueOf(remainedSecs%60).length()==1){
                        timer.setText((remainedSecs / 60) + ":" + "0"+ (remainedSecs % 60));// manage it accordign to you
                    }else {
                        timer.setText((remainedSecs / 60) + ":" + (remainedSecs % 60));
                    }
                }
                public void onFinish() {
                    timer.setText("0:00");
                    workingt.cancel();
                    start_count=0;
//                restingt.start();
//                totalworkingtime_day+=workingtime/60/1000;

                    //notify the user when time is up
                    mgr.notify(NOTIFY_ID, mBuilder.build());
                }
            };
        workingt.start();}

    }

    public void start2(){
        start_count++;
        if (start_count==1){
            setWorkingTime(sharedPref.getInt("promodoroDuration",25));
            workingt = new CountDownTimer(workingtime, 1000) {
                public void onTick(long millisUntilFinished) {
                    long remainedSecs = millisUntilFinished / 1000;
                    if(String.valueOf(remainedSecs%60).length()==1){
                        timer.setText((remainedSecs / 60) + ":" + "0"+ (remainedSecs % 60));// manage it accordign to you
                    }else {
                        timer.setText((remainedSecs / 60) + ":" + (remainedSecs % 60));
                    }
                }
                public void onFinish() {
                    timer.setText("0:00");
                    workingt.cancel();
                    start_count=0;
//                restingt.start();
//                totalworkingtime_day+=workingtime/60/1000;

                    //notify the user when time is up
                    mgr.notify(NOTIFY_ID, mBuilder.build());
                }
            };
            workingt.start();}

    }

    public void stop(View v){
        workingt.cancel();
//        restingt.cancel();
        setWorkingTime(sharedPref.getInt("promodoroDuration",25));
        timer.setText(workingtime/60/1000+":00");
        start_count=0;

    }
//drop down menu
    public void showMenu(View view) {
        if (dropDownMenuIconItem.getVisibility() == View.VISIBLE) {
            dropDownMenuIconItem.setVisibility(View.INVISIBLE);
            Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.anim_back);
            animation.setDuration(270);
            dropDownMenuIconItem.setAnimation(animation);
            dropDownMenuIconItem.animate();
            animation.start();
        } else {
            dropDownMenuIconItem.setVisibility(View.VISIBLE);
            Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.anim);
            animation.setDuration(270);
            dropDownMenuIconItem.setAnimation(animation);
            dropDownMenuIconItem.animate();
            animation.start();
        }
    }

    public void openSettings(View v){
        Intent openSettings = new Intent(this,Settings.class);
        startActivity(openSettings);
    }
    public void openSummary(View v){
        Intent openSummary = new Intent(this,summary.class);
        startActivity(openSummary);

    }

    public int getTotalworkingtime_day(){
        return totalworkingtime_day;
    }


    // TODO: 13/12/16 requesting current state of the totem, if it's in coding state, start the timer
    public boolean requestState() {
        int userId = sharedPref.getInt(getString(R.string.prefsUserId),0);
        String newURL = APIUrl + "totemdatum/latest_state?id=" + userId;
        getRequest = new JsonObjectRequest(Request.Method.GET, newURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        //Toast.makeText(getApplication().getBaseContext(), response.get("age").toString(),Toast.LENGTH_SHORT).show();
                        Log.d("Response", response.toString());
                        mAuthTask = null;
                        checkState(response);
//                        Toast.makeText(getApplicationContext(),answer+"",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        checkState(null);
                    }
                }
        );
        queue.add(getRequest);
        return answer;

    }

    public boolean checkState(final JSONObject response){
        try {
            SharedPreferences.Editor editor = sharedPref.edit();
            String state = response.getString("state");
            editor.putString(getString(R.string.currentState),state);
//            Toast.makeText(getApplicationContext(),state,Toast.LENGTH_SHORT).show();
            if(state.equals("Working")&&response.getBoolean("is_current_state")){
                String startTime = response.getString("created_at");
                String updateTime = response.getString("updated_at");
                editor.putString(getString(R.string.startTime),startTime);
                editor.putString(getString(R.string.updateTime),updateTime);
                editor.commit();

                answer = true;
//                Toast.makeText(getApplicationContext(),answer+"",Toast.LENGTH_SHORT).show();
            }
            return answer;
        }catch (Exception e){
//            Toast.makeText(getApplicationContext(),"nothing",Toast.LENGTH_SHORT).show();
            return answer;
        }
    }
}

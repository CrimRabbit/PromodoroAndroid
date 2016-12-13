package com.example.ruochenzhang.iot_timer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import HTTP.Person;

public class Login extends AppCompatActivity implements LoaderCallbacks<Cursor> {

//    private static final int REQUEST_READ_CONTACTS = 0;

    private String APIUrl = "http://iotfocus.herokuapp.com/api/";
    //Keep track of the login task to ensure we can cancel it if requested.
    private Object mAuthTask = null;
    public SharedPreferences sharedPref;

    private int personId=-1;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mAgeView;
    private Spinner genderSpin;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout nameLay;
    private TextInputLayout dataLay;
    //protected UserLoginTask.Person mPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_name);
        mAgeView = (EditText) findViewById(R.id.login_age);
        nameLay = (TextInputLayout) findViewById(R.id.login_nameLayout);
        dataLay = (TextInputLayout) findViewById(R.id.login_dataLayout);
        genderSpin = (Spinner)findViewById(R.id.login_genderSpinner);
        List<String> genderlist = Arrays.asList(getResources().getStringArray(R.array.gender));
        ArrayAdapter<String> Padapter = new ArrayAdapter<String>(getBaseContext(),R.layout.genderspinner,genderlist);
        Padapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpin.setAdapter(Padapter);

        Button mEmailSignInButton = (Button) findViewById(R.id.login_loginButton);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /*
        For screenchange from name to data.
     */
    private void screenChange(){
        Button nameButton = (Button) findViewById(R.id.login_loginButton);
        Button dataButton = (Button) findViewById(R.id.login_bioButton);
        nameButton.setVisibility(View.GONE);
        dataButton.setVisibility(View.VISIBLE);

        nameLay.setVisibility(View.GONE);
        dataLay.setVisibility(View.VISIBLE);

        dataButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(); //TODO
            }
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        //mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String gender = genderSpin.getSelectedItem().toString();
        String age = mAgeView.getText().toString();

        //String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) && mEmailView.getVisibility() == View.VISIBLE) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email) && mEmailView.getVisibility() == View.VISIBLE) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        else if (TextUtils.isEmpty(age) && dataLay.getVisibility() == View.VISIBLE){
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            //net access
            RequestQueue queue = Volley.newRequestQueue(this);
            HashMap<String,String> personData = new HashMap<String,String>();
            personData.put("name",mEmailView.getText().toString());
            JSONObject sendit = new JSONObject(personData);
            JsonObjectRequest getRequest = null;

            //new or misinformed person, getting more data.
            if(dataLay.getVisibility() == View.VISIBLE){
                String newURL = APIUrl + "person";
                Person reqPerson = new Person(Integer.parseInt(age),email,gender.charAt(0),"0");
                if(personId != -1){
                    //not a new person
                    reqPerson.setId(personId);
                    Gson g = new Gson();
                    JSONObject jsonObj=null;
                    Log.d("JSON Object to put",g.toJson(reqPerson));
                    try {
                        jsonObj = new JSONObject(g.toJson(reqPerson));
                    }catch(JSONException je){
                        Log.d("Error.JSONException", je.toString());
                    }
                    Log.d("JSON Object b4 volley",g.toJson(reqPerson));
                    getRequest = new JsonObjectRequest(Request.Method.PUT, newURL,jsonObj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // display response
                                    //Toast.makeText(getApplication().getBaseContext(), response.get("age").toString(),Toast.LENGTH_SHORT).show();
                                    Log.d("Response", response.toString());
                                    mAuthTask = null;
                                    showProgress(false);
                                    personResult(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error.Response", error.toString());
                                    mAuthTask = null;
                                    showProgress(false);
                                    personResult(null);
                                }
                            }
                    );
                }
                else{
                    //new person
                    Gson g = new Gson();
                    JSONObject jsonObj=null;
                    Log.d("JSON Object to post",g.toJson(reqPerson));
                    try {
                        jsonObj = new JSONObject(g.toJson(reqPerson));
                    }catch(JSONException je){
                        Log.d("Error.JSONException", je.toString());
                    }
                    getRequest = new JsonObjectRequest(Request.Method.POST, newURL,jsonObj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // display response
                                    //Toast.makeText(getApplication().getBaseContext(), response.get("age").toString(),Toast.LENGTH_SHORT).show();
                                    Log.d("Response", response.toString());
                                    mAuthTask = null;
                                    showProgress(false);
                                    personResult(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error.Response", error.toString());
                                    mAuthTask = null;
                                    showProgress(false);
                                    personResult(null);
                                }
                            }
                    );
                }

            }
            //normal person, can pass.
            else {
                String newURL = APIUrl + "person/by_name?name=" + mEmailView.getText().toString();
                getRequest = new JsonObjectRequest(Request.Method.GET, newURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                //Toast.makeText(getApplication().getBaseContext(), response.get("age").toString(),Toast.LENGTH_SHORT).show();
                                Log.d("Response", response.toString());
                                mAuthTask = null;
                                showProgress(false);
                                personResult(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.toString());
                                mAuthTask = null;
                                showProgress(false);
                                personResult(null);
                            }
                        }
                );
            }
            mAuthTask = new Object();
            queue.add(getRequest);
        }
    }



    private void personResult(final JSONObject returns){
        try{
            if(returns==null){
                screenChange();
            }else if(returns.getInt("age") == 0 || (returns.get("gender").toString().charAt(0)) == 'U'){
                personId = returns.getInt("id");
                screenChange();
            }
            else{
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.prefsUserId),returns.getInt("id"));
                editor.putString(getString(R.string.prefsUserName),returns.getString("name"));
                editor.putInt(getString(R.string.prefsUserAge),returns.getInt("age"));
                if(returns.get("gender").toString().charAt(0) == 'M'){
                    editor.putString(getString(R.string.prefsUserGender),"Male");
                }else if(returns.get("gender").toString().charAt(0) == 'F'){
                    editor.putString(getString(R.string.prefsUserGender),"Female");
                }else{
                    editor.putString(getString(R.string.prefsUserGender),"Others");
                }

                editor.commit();

                Intent nextIntent = new Intent(getBaseContext(),timer.class); //TODO
                startActivity(nextIntent);
            }
        }
        catch(Exception e){
            Toast.makeText(getApplication().getBaseContext(), "haha help",Toast.LENGTH_SHORT).show();
            Log.d("Error.Response", e.toString());
        }
    }

    private boolean isEmailValid(String email) {
        //to add control for illegal usernames here
        return true;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


}


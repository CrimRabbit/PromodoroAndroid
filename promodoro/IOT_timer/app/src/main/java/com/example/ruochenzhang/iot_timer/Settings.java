package com.example.ruochenzhang.iot_timer;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class Settings extends ListActivity {
    private SettingsAdapter sAdapter;
    public SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        //setContentView(R.layout.activity_settings);
//        final ArrayAdapter adapter = new ArrayAdapter(this,R.layout.settings_toggle,R.id.settings_listviewTextview,getResources().getStringArray(R.array.settingsList));
//        ListView listView = (ListView)findViewById(R.id.settings_toggle);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Object item = parent.getItemAtPosition(position);
//                Toast.makeText(getApplication().getBaseContext(),item.toString(),Toast.LENGTH_SHORT).show();
//            }
//        });
        sAdapter = new SettingsAdapter();
        String[] stringArr = getResources().getStringArray(R.array.settingsList);
        for(int j = 0;j < stringArr.length;j++ ){
            if(j > 4){
                sAdapter.addTextItem(stringArr[j]);
            }
            else if(j > 1){
                sAdapter.addToggleItem(stringArr[j]);

            }
            else {
                sAdapter.addItem(stringArr[j]);
            }
        }
        setListAdapter(sAdapter);
    }

    private class SettingsAdapter extends BaseAdapter{
        private static final int TYPE_BUTTON = 0;
        private static final int TYPE_TOGGLE = 1;
        private static final int TYPE_TEXT = 2;
        private static final int TYPE_TOTALNUM = TYPE_TEXT + 1;

        private ArrayList ldata = new ArrayList();
        private LayoutInflater lInflater;
        private TreeSet ltoggleSet = new TreeSet();
        private TreeSet ttoggleSet = new TreeSet();
        Context context = getApplication().getBaseContext();

        public SettingsAdapter(){
            lInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void toggled(int position){
            //Toast.makeText(getApplication().getBaseContext(), String.valueOf(position),Toast.LENGTH_LONG).show();
            switch(position){
                case 0:
                    //duration - done in getview
                    break;
                case 1:
                    //rest - done in getview
                    break;
                case 2:
                    //notifications
                    int notifications = sharedPref.getInt(getString(R.string.settingsNotifications),0);
                    if(notifications == 1){
                        //if keep on, set to off
                        Toast.makeText(getApplication().getBaseContext(), "no notifications",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.settingsNotifications),0);
                        editor.commit();
                    }else{
                        Toast.makeText(getApplication().getBaseContext(), "notify",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.settingsNotifications),1);
                        editor.commit();
                    }
                    break;
                case 3:
                    //sound on timer 0 - all this does is set sharedPref
                    int mute = sharedPref.getInt(getString(R.string.settingsMute),1);
                    if(mute == 1){
                        //if keep on, set to off
                        Toast.makeText(getApplication().getBaseContext(), "not muted",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.settingsMute),0);
                        editor.commit();
                    }else{
                        Toast.makeText(getApplication().getBaseContext(), "muted",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.settingsMute),1);
                        editor.commit();
                    }
                    break;
                case 4:
                    //screen on
                    int showScreen = sharedPref.getInt(getString(R.string.settingsShowScreen),1);
                    if(showScreen == 1){
                        //if keep on, set to off
                        Toast.makeText(getApplication().getBaseContext(), "off",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.settingsShowScreen),0);
                        editor.commit();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }else{
                        Toast.makeText(getApplication().getBaseContext(), "on",Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.settingsShowScreen),1);
                        editor.commit();
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    break;
                case 5:
                    //clear history
                    break;
                case 6:
                    //current user

                    break;
            }
        }

        //text & button
        public void addItem(final String item){
            ldata.add(item);
            notifyDataSetChanged();
        }

        public void addToggleItem(final String item){
            ldata.add(item);
            ltoggleSet.add(ldata.size()-1); //add last item
            notifyDataSetChanged();
        }

        public void addTextItem(final String item){
            ldata.add(item);
            ttoggleSet.add(ldata.size()-1); //add last item
            notifyDataSetChanged();
        }

        public int getItemViewType(int position){
            if(ltoggleSet.contains(position)){
                return TYPE_TOGGLE;
            }else if(ttoggleSet.contains(position)){
                return TYPE_TEXT;
            }else{
                return TYPE_BUTTON;
            }
            //should modify to handle more if necessary.
        }

        public int getViewTypeCount(){
            return TYPE_TOTALNUM;
        }

        @Override
        public int getCount() {
            return ldata.size();
        }

        @Override
        public Object getItem(int position) {
            return ldata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            int type = getItemViewType(position);
            if (convertView == null) {

                holder = new ViewHolder();
                int set;
                int promodoroTimingSelected;
                int restTimingSelected;
                switch(type){
                    case TYPE_BUTTON:
                        convertView = lInflater.inflate(R.layout.settings_duration, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.settings_listviewButtonTextview);
                        holder.spinspin = (Spinner)convertView.findViewById(R.id.settings_listviewButtonSmallText);
                        //TODO set spinspin
                        switch(position){
                            case 0:
                                List<String> myOptions = Arrays.asList((getResources().getStringArray(R.array.promodoroDuration)));
//                                int value = myOptions.indexOf(promodoroTimingSelected+" Minutes");
//                                set = sharedPref.getInt(String.valueOf(value),2);
//                                Toast.makeText(getApplication().getBaseContext(),value+"",Toast.LENGTH_SHORT).show();
                                 promodoroTimingSelected = sharedPref.getInt("promodoroDuration",25);
                                set = myOptions.indexOf(promodoroTimingSelected+" Minutes");
//                                set = sharedPref.getInt("promodoroDuration",25);
//                                set = sharedPref.getInt(getString(R.string.settingsPromodoroDuration),2);
                                holder.spinspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selected = parent.getItemAtPosition(position).toString();
                                        //show current choosing option
                                        Toast.makeText(getApplication().getBaseContext(), selected,Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
                                        String time = selected.substring(0,selected.indexOf(" "));
//                                        Resources res = getResources();
//                                        String text = String.format(res.getString(R.string.settingsPromodoroDuration),time);
//                                        promodoroTimingSelected = sharedPref.getInt("promodoroDuration",25);
                                        editor.clear();
                                        editor.putInt("promodoroDuration",Integer.parseInt(time));
                                        editor.apply();
//                                        Toast.makeText(getApplicationContext(),getString(R.string.settingsPromodoroDuration),Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        //do nothing
                                    }
                                });
                                List<String> Pdurations = Arrays.asList(getResources().getStringArray(R.array.promodoroDuration));
                                ArrayAdapter<String> Padapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_item,Pdurations);
                                Padapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                holder.spinspin.setAdapter(Padapter);
                                holder.spinspin.setSelection(set,true);
                                break;
                            case 1:
                                restTimingSelected = sharedPref.getInt("restDuration",5);
                                List<String> myOptions2 = Arrays.asList((getResources().getStringArray(R.array.restDuration)));
                                set =  myOptions2.indexOf(restTimingSelected+" Minutes");
//                                set = sharedPref.getInt(getString(R.string.settingsRestDuration),1);
//                               set = sharedPref.getInt("restDuration",5);
                                holder.spinspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selected = parent.getItemAtPosition(position).toString();
                                        Toast.makeText(getApplication().getBaseContext(), selected,Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
                                        String time = selected.substring(0,selected.indexOf(" "));
                                        editor.putInt("restDuration",Integer.parseInt(time));
                                        editor.clear();
                                        editor.apply();

                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        //do nothing
                                    }
                                });
                                List<String> Rdurations = Arrays.asList(getResources().getStringArray(R.array.restDuration));
                                ArrayAdapter<String> Radapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_item,Rdurations);
                                Radapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                holder.spinspin.setAdapter(Radapter);
                                holder.spinspin.setSelection(set,true);
                                break;
                            case 5:
                                break;
                        }
                        break;
                    case TYPE_TOGGLE:
                        convertView = lInflater.inflate(R.layout.settings_toggle, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.settings_listviewToggleTextview);
                        holder.toggle = (Switch)convertView.findViewById(R.id.settings_listviewSwitch);

                        switch(position){
                            case 2:
                                set = sharedPref.getInt(getString(R.string.settingsNotifications),0);
                                if(set == 1){
                                    holder.toggle.setChecked(true);
                                }
                                break;
                            case 3:
                                set = sharedPref.getInt(getString(R.string.settingsShowScreen),0);
                                if(set == 1){
                                    holder.toggle.setChecked(true);
                                }
                                break;
                            case 4:
                                set = sharedPref.getInt(getString(R.string.settingsShowScreen),1);
                                if(set == 1){
                                    holder.toggle.setChecked(true);
                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }else{
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }
                                break;
                        }

                        //holder.toggle.setChecked();
                        holder.toggle.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                toggled(position);
                            }
                        });
                        break;
                    case TYPE_TEXT:
                        convertView = lInflater.inflate(R.layout.settings_text, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.settings_textButtonTextview); //TODO
                        holder.smalltext = (TextView) convertView.findViewById(R.id.settings_textButtonSmallText);
                        if(position==6){
                            //to set current user
                            holder.smalltext.setText(sharedPref.getString(getString(R.string.prefsUserName),""));
                        }
                        holder.textView.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                toggled(position);
                            }
                        });
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText((String)ldata.get(position));
            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView smalltext;
        public Spinner spinspin;
        public Switch toggle;
    }

}

package com.example.ruochenzhang.iot_timer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class summary extends AppCompatActivity {
    private List<String> durations ;
    private List<String> states;
    private List<PieEntry> entries ;
    private int total_time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
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
        entries.add(new PieEntry(18.5f, "Meeting"));
        entries.add(new PieEntry(26.7f, "Coffee"));
        entries.add(new PieEntry(4.0f, "Break"));
        entries.add(new PieEntry(30.8f, "Coding"));
        entries.add(new PieEntry(17f,"Research"));
        entries.add(new PieEntry(3f,"Email"));

        PieDataSet set = new PieDataSet(entries, "Election Results");
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
}

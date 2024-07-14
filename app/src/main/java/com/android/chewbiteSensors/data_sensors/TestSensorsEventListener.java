package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestSensorsEventListener implements SensorEventListener {

    private final SensorManager sensorManager;

    LineChart chart;
    LineData lineData;

    HashMap<Integer, LineDataSet> sensorDataSet;
    List<Sensor> registeredSensors;

    long currentIndex = 0;

    TestSensorsEventListener(Context context, LineChart chart) {
        this.chart = chart;
        this.lineData = new LineData();
        this.chart.setData(this.lineData);
        this.chart.getDescription().setText("");

        Legend legend = this.chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        legend.setTextSize(12);

        this.sensorDataSet = new HashMap<>();
        this.registeredSensors = new ArrayList<>();

        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        this.addSensor(Sensor.TYPE_ACCELEROMETER, CBBuffer.STRING_ACCELEROMETER,  Color.RED);
        this.addSensor(Sensor.TYPE_GYROSCOPE, CBBuffer.STRING_GYROSCOPE, Color.GREEN);
        this.addSensor(Sensor.TYPE_MAGNETIC_FIELD, CBBuffer.STRING_MAGNETIC_FIELD, Color.BLUE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float mod = (float) Math.sqrt(event.values[0] * event.values[0]
                + event.values[1] * event.values[1]
                + event.values[2] * event.values[2]);
        this.addDataSetEntry(new Entry(currentIndex, mod), event.sensor.getType());
        this.currentIndex++;
    }

    public void stop() {
        for (Sensor sensor: this.registeredSensors) {
            this.sensorManager.unregisterListener(this, sensor);
        }
    }

    private void addDataSetEntry(Entry entry, Integer sensorType) {
        LineDataSet dataSet = this.sensorDataSet.get(sensorType);

        if (dataSet != null) {
            if (this.getChartEntryCount() > 1000) {
                this.removeOldestEntry();
            }
            dataSet.addEntry(entry);
            this.lineData.notifyDataChanged();
            this.chart.notifyDataSetChanged();
            this.chart.invalidate();
        }
    }

    private void addSensor(Integer sensorType, String label, int color) {
        Sensor sensor = this.sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            LineDataSet dataSet = new LineDataSet(new ArrayList<>(), label);
            dataSet.setDrawCircles(false);
            dataSet.setColor(color);

            this.sensorDataSet.put(sensorType, dataSet);
            this.lineData.addDataSet(dataSet);

            this.registeredSensors.add(sensor);
        }
    }

    private void removeOldestEntry() {
        Iterator<Map.Entry<Integer, LineDataSet>> it = this.sensorDataSet.entrySet().iterator();
        Map.Entry<Integer, LineDataSet> first = it.next();
        LineDataSet oldestDataSet = first.getValue();
        Entry oldestEntry = oldestDataSet.getEntryForIndex(0);

        while (it.hasNext()) {
            Map.Entry<Integer, LineDataSet> pair = it.next();
            LineDataSet dataSet = pair.getValue();
            Entry entry = dataSet.getEntryForIndex(0);
            if (entry.getX() < oldestEntry.getX()) {
                oldestDataSet = dataSet;
                oldestEntry = entry;
            }
        }

        oldestDataSet.removeFirst();
    }

    private int getChartEntryCount() {
        int count = 0;
        for (Map.Entry<Integer, LineDataSet> integerLineDataSetEntry : this.sensorDataSet.entrySet()) {
            LineDataSet dataSet = integerLineDataSetEntry.getValue();
            count += dataSet.getEntryCount();
        }
        return count;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
}

package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
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

    public TestSensorsEventListener(Context context, LineChart chart, Integer sensor_Type, String sensor_label, int sensor_color) {
        this.chart = chart;
        this.lineData = new LineData();
        this.chart.setData(this.lineData);
        this.chart.getDescription().setText("");

        Legend legend = this.chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12);

        this.sensorDataSet = new HashMap<>();
        this.registeredSensors = new ArrayList<>();

        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        //this.addSensor(Sensor.TYPE_ACCELEROMETER, CBBuffer.STRING_ACCELEROMETER,  Color.RED);
        //this.addSensor(Sensor.TYPE_GYROSCOPE, CBBuffer.STRING_GYROSCOPE, Color.GREEN);
        //this.addSensor(Sensor.TYPE_MAGNETIC_FIELD, CBBuffer.STRING_MAGNETIC_FIELD, Color.BLUE);
        this.addSensor(sensor_Type, sensor_label,  sensor_color);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float mod = (float) Math.sqrt(event.values[0] * event.values[0]
                + event.values[1] * event.values[1]
                + event.values[2] * event.values[2]);

        // Verificar si el DataSet está vacío
        LineDataSet dataSet = this.sensorDataSet.get(event.sensor.getType());
        if (dataSet != null){
            if (dataSet.getEntryCount() > 0) {
                // Si el conjunto de datos ya tiene entradas, agrega una nueva entrada
                this.addDataSetEntry(new Entry(this.currentIndex, mod), event.sensor.getType());
            } else {
                // Si el conjunto de datos está vacío, agrega la primera entrada
                dataSet.addEntry(new Entry(0, mod));
            }
            this.currentIndex++;
        }

        //this.addDataSetEntry(new Entry(currentIndex, mod), event.sensor.getType());
        //this.currentIndex++;

    }

    public void stop() {
        for (Sensor sensor: this.registeredSensors) {
            this.sensorManager.unregisterListener(this, sensor);
        }
    }

    private void addDataSetEntry(Entry entry, Integer sensorType) {

        LineDataSet dataSet = this.sensorDataSet.get(sensorType);
        if (dataSet == null) {
            // Verifica si el DataSet para este tipo de sensor ya existe
            // Si no, créalo y agrégalo a sensorDataSet y lineData
            dataSet = new LineDataSet(new ArrayList<>(), "Sensor " + sensorType);
            this.sensorDataSet.put(sensorType, dataSet);
            this.lineData.addDataSet(dataSet);
        }

        // Verifica si el conjunto de datos ya tiene una entrada con el mismo índice
        if (dataSet.getEntryCount() > 0 && dataSet.getEntryForIndex(0).getX() == entry.getX()) {
            // Si ya existe una entrada con el mismo índice, actualiza su valor
            dataSet.getEntryForIndex(0).setY(entry.getY());
        } else {
            // Si no existe una entrada con el mismo índice, agrega la nueva entrada
            if (this.getChartEntryCount() > 400) {
                this.removeOldestEntry();
            }
            dataSet.addEntry(entry);
        }

        this.lineData.notifyDataChanged();
        this.chart.notifyDataSetChanged();
        this.chart.invalidate();
    }

    private void addSensor(Integer sensorType, String label, int color) {
        Sensor sensor = this.sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            LineDataSet dataSet = new LineDataSet(new ArrayList<>(), label);
            dataSet.setDrawCircles(false);
            dataSet.setColor(color);

            // Agrega una entrada al conjunto de datos
            dataSet.addEntry(new Entry(0, 0));

            this.sensorDataSet.put(sensorType, dataSet);
            this.lineData.addDataSet(dataSet);

            this.registeredSensors.add(sensor);
        }
    }

    private void removeOldestEntry() {
        Iterator<Map.Entry<Integer, LineDataSet>> it = this.sensorDataSet.entrySet().iterator();
        // Verificar si hay algún DataSet antes de continuar
        if (!it.hasNext()) {
            return; // Si no hay DataSets, salir del método
        }

        Map.Entry<Integer, LineDataSet> first = it.next();
        LineDataSet oldestDataSet = first.getValue();

        // Verificar si el primer DataSet tiene entradas
        if (oldestDataSet.getEntryCount() > 0) {
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

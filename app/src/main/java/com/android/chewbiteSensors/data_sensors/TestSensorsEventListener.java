package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.SENSOR_SERVICE;
import static android.graphics.Paint.Align.RIGHT;

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

    //HashMap<Integer, LineDataSet> sensorDataSet;
    private HashMap<Integer, List<LineDataSet>> sensorDataSets;
    List<Sensor> registeredSensors;

    long currentIndex = 0;

    public TestSensorsEventListener(Context context, LineChart chart, Integer sensor_Type, String sensor_label, int sensor_color, float yMin, float yMax) {
        this.chart = chart;
        this.lineData = new LineData();
        this.chart.setData(this.lineData);
        this.chart.getDescription().setText("");

        // Configura el eje Y con límites fijos
        this.chart.getAxisLeft().setAxisMinimum(yMin);
        this.chart.getAxisLeft().setAxisMaximum(yMax);
        this.chart.getAxisRight().setEnabled(false); // Desactiva el eje Y derecho si no lo necesitas

        // Configura el eje X para que aparezca en la parte inferior
        this.chart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);

        Legend legend = this.chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        legend.setTextSize(12);

        this.sensorDataSets = new HashMap<>();
        this.registeredSensors = new ArrayList<>();

        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        // Reinicia el índice de datos
        this.currentIndex = 0; // Reinicia el índice

        //this.addSensor(Sensor.TYPE_ACCELEROMETER, CBBuffer.STRING_ACCELEROMETER,  Color.RED);
        //this.addSensor(Sensor.TYPE_GYROSCOPE, CBBuffer.STRING_GYROSCOPE, Color.GREEN);
        //this.addSensor(Sensor.TYPE_MAGNETIC_FIELD, CBBuffer.STRING_MAGNETIC_FIELD, Color.BLUE);
        this.addSensor(sensor_Type, sensor_label, sensor_color);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*float mod = (float) Math.sqrt(event.values[0] * event.values[0]
                + event.values[1] * event.values[1]
                + event.values[2] * event.values[2]);*/
        // Obtener los valores de los ejes X, Y, Z
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Verificar si el DataSet está vacío
        //LineDataSet dataSet = this.sensorDataSet.get(event.sensor.getType());
        List<LineDataSet> dataSets = this.sensorDataSets.get(event.sensor.getType());
        if (dataSets != null) {
            //if (dataSet.getEntryCount() > 0) {
                // Si el conjunto de datos ya tiene entradas, agrega una nueva entrada
                //this.addDataSetEntry(new Entry(this.currentIndex, mod), event.sensor.getType());
                this.addDataSetEntry(new Entry(this.currentIndex, x), event.sensor.getType(), 0); // Eje X
                this.addDataSetEntry(new Entry(this.currentIndex, y), event.sensor.getType(), 1); // Eje Y
                this.addDataSetEntry(new Entry(this.currentIndex, z), event.sensor.getType(), 2); // Eje Z
            /*} else {
                // Si el conjunto de datos está vacío, agrega la primera entrada
                //dataSet.addEntry(new Entry(0, mod));
                this.addDataSetEntry(new Entry(this.currentIndex, 0), event.sensor.getType(), 0); // Eje X
                this.addDataSetEntry(new Entry(this.currentIndex, 0), event.sensor.getType(), 1); // Eje Y
                this.addDataSetEntry(new Entry(this.currentIndex, 0), event.sensor.getType(), 2); // Eje Z
            }*/
            this.currentIndex++;
        }

        //this.addDataSetEntry(new Entry(currentIndex, mod), event.sensor.getType());
        //this.currentIndex++;

    }

    public void stop() {
        for (Sensor sensor : this.registeredSensors) {
            this.sensorManager.unregisterListener(this, sensor);
        }
    }

    private void addDataSetEntry(Entry entry, Integer sensorType, int axisIndex) {

        //LineDataSet dataSet = this.sensorDataSet.get(sensorType);
        List<LineDataSet> dataSets = this.sensorDataSets.get(sensorType);
        if (dataSets == null) {
            // Verifica si el DataSet para este tipo de sensor ya existe
            // Si no, créalo y agrégalo a sensorDataSet y lineData
            //dataSet = new LineDataSet(new ArrayList<>(), "Sensor " + sensorType);
            dataSets = new ArrayList<>();
            //this.sensorDataSet.put(sensorType, dataSet);
            this.sensorDataSets.put(sensorType, dataSets);
            //this.lineData.addDataSet(dataSet);

            // Crear y agregar LineDataSet para cada eje
            for (int i = 0; i < 3; i++) {
                LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Eje " + (char) ('X' + i)); // 'X', 'Y', 'Z'
                dataSet.setDrawCircles(false);
                dataSet.setColor(i == 0 ? Color.RED : (i == 1 ? Color.GREEN : Color.BLUE)); // Colores para X, Y, Z
                dataSet.setDrawValues(false); // Desactivar etiquetas de puntos de datos
                dataSets.add(dataSet);
                this.lineData.addDataSet(dataSet);
            }
        }

        // Agregar la entrada al conjunto de datos correspondiente
        LineDataSet dataSet = dataSets.get(axisIndex);
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
            /*LineDataSet dataSet = new LineDataSet(new ArrayList<>(), label);
            dataSet.setDrawCircles(false);
            dataSet.setColor(color);
            dataSet.setDrawValues(false); // Disable data point labels

            // Agrega una entrada al conjunto de datos
            dataSet.addEntry(new Entry(0, 0));*/
            // Crear y agregar LineDataSet para cada eje (X, Y, Z)
            // Crear una nueva lista de LineDataSet para este sensor
            List<LineDataSet> dataSets = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                //LineDataSet dataSet = new LineDataSet(new ArrayList<>(), label + " " + (char) ('X' + i)); // 'X', 'Y', 'Z'
                LineDataSet dataSet = new LineDataSet(new ArrayList<>(), " " + (char) ('X' + i)); // 'X', 'Y', 'Z'
                dataSet.setDrawCircles(false);
                dataSet.setColor(i == 0 ? Color.RED : (i == 1 ? Color.GREEN : Color.BLUE)); // Colores para X, Y, Z
                dataSet.setDrawValues(false); // Desactivar etiquetas de puntos de datos
                dataSets.add(dataSet);
                this.lineData.addDataSet(dataSet);
            }

            this.sensorDataSets.put(sensorType, dataSets);
            //this.lineData.addDataSet(dataSet);

            this.registeredSensors.add(sensor);
        }
    }

    /*private void removeOldestEntry() {
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

    }*/

    /**
     * Elimina la entrada más antigua de cada conjunto de datos
     */
    private void removeOldestEntry() {
        Iterator<Map.Entry<Integer, List<LineDataSet>> > it = this.sensorDataSets.entrySet().iterator();
        // Verificar si hay algún DataSet antes de continuar
        if (!it.hasNext()) {
            return; // Si no hay DataSets, salir del método
        }

        // Inicializar el DataSet más antiguo
        LineDataSet oldestDataSet = null;
        Entry oldestEntry = null;

        // Iterar sobre cada lista de LineDataSet
        while (it.hasNext()) {
            Map.Entry<Integer, List<LineDataSet>> pair = it.next();
            List<LineDataSet> dataSets = pair.getValue();

            for (LineDataSet dataSet : dataSets) {
                // Verificar si el DataSet tiene entradas
                if (dataSet.getEntryCount() > 0) {
                    Entry entry = dataSet.getEntryForIndex(0);
                    // Si no se ha encontrado un DataSet más antiguo o si este es más antiguo
                    if (oldestEntry == null || entry.getX() < oldestEntry.getX()) {
                        oldestDataSet = dataSet;
                        oldestEntry = entry;
                    }
                }
            }
        }

        // Si se encontró un DataSet más antiguo, eliminar la entrada más antigua
        if (oldestDataSet != null) {
            oldestDataSet.removeFirst();
        }
    }

    private int getChartEntryCount() {
        int count = 0;
        for (Map.Entry<Integer, List<LineDataSet>> entry : this.sensorDataSets.entrySet()) {
            List<LineDataSet> dataSets = entry.getValue();
            for (LineDataSet dataSet : dataSets) {
                count += dataSet.getEntryCount();
            }
        }
        return count;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
}

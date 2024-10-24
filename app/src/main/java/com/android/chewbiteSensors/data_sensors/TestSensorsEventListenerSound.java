package com.android.chewbiteSensors.data_sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TestSensorsEventListenerSound implements Runnable {

    private LineChart chart;
    private LineData lineData;
    private HashMap<Integer, LineDataSet> sensorDataSet;
    private Context context; // Referencia al Context

    private boolean isRecording = false;

    public TestSensorsEventListenerSound(Context context, LineChart chart, String sensor_label, int sensor_color) {

        this.context = context; // Guarda la referencia al Context

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

        // Crea un nuevo LineDataSet para el audio
        //LineDataSet dataSet = new LineDataSet(new ArrayList<>(), sensor_label);
        LineDataSet dataSet = new LineDataSet(Collections.singletonList(new Entry(0, 0)), sensor_label); // Agrega un valor inicial (0, 0)
        dataSet.setDrawCircles(false);
        dataSet.setColor(sensor_color);

        this.sensorDataSet.put(0, dataSet); // Usa 0 como clave para el audio
        this.lineData.addDataSet(dataSet);
    }

    public void startRecording() {
        isRecording = true;
        new Thread(this).start();
    }

    public void stopRecording() {
        isRecording = false;
    }

    @Override
    public void run() {
        int sampleRate = 44100; // Frecuencia de muestreo
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);

        short[] buffer = new short[bufferSize];
        audioRecord.startRecording();

        while (isRecording) {
            int bytesRead = audioRecord.read(buffer, 0, bufferSize);

            if (bytesRead > 0) {
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < bytesRead; i++) {
                    entries.add(new Entry(i, buffer[i]));
                }

                final LineDataSet dataSet = sensorDataSet.get(0); // Obtén el LineDataSet para el audio
                if (dataSet != null) {
                    dataSet.setValues(entries); // Actualiza los datos

                    // Verifica si el LineDataSet tiene datos antes de actualizar el gráfico
                    if (dataSet.getEntryCount() > 0) {
                        // Actualiza el gráfico en el hilo principal
                        chart.post(new Runnable() {
                            @Override
                            public void run() {
                                lineData.notifyDataChanged();
                                chart.notifyDataSetChanged();
                                if (dataSet != null && dataSet.getEntryCount() > 0) {
                                    chart.invalidate();
                                }
                            }
                        });
                    }
                }
            }
        }

        audioRecord.stop();
        audioRecord.release();
    }
}

package com.android.chewbiteSensors.data_sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

// Clase para el sensor de audio
public class TestSensorsEventListenerAudio {
    private AudioRecord audioRecord = null;  // Inicializado como null por defecto
    private final int bufferSize;
    private final LineChart chart;
    private final LineData lineData;
    private final LineDataSet dataSet;

    private long currentIndex = 0;
    private final Handler handler = new Handler();
    private final int updateInterval = 1; // Actualiza el gráfico cada 100 ms

    public TestSensorsEventListenerAudio(Context context, LineChart chart, String sensor_label, int sensor_color) {
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

        this.dataSet = new LineDataSet(new ArrayList<>(), sensor_label);
        this.dataSet.setDrawCircles(false);
        this.dataSet.setColor(sensor_color);

        // Agrega un punto inicial al dataSet para evitar el error de índice vacío
        dataSet.addEntry(new Entry(0, 0));
        this.lineData.addDataSet(this.dataSet);

        // Configura el formateador personalizado para el eje X
        chart.getXAxis().setValueFormatter(new TimeXAxisValueFormatter());

        // Configura el AudioRecord para capturar audio
        bufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        // Verifica si el permiso de grabación ha sido otorgado
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
        // Crea un nuevo AudioRecord con el tamaño de búfer calculado
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
    }

    public void startRecording() {
        // Asegúrate de que audioRecord no es null antes de intentar iniciar la grabación
        if (audioRecord != null) {
            audioRecord.startRecording();
            handler.post(updateChartRunnable); // Inicia la actualización del gráfico
        } else {
            // Puedes mostrar un mensaje indicando que no se pudo iniciar la grabación
        }
    }

    public void stopRecording() {
        // Asegúrate de que audioRecord no es null antes de intentar detener la grabación
        if (audioRecord != null) {
            audioRecord.stop();
            handler.removeCallbacks(updateChartRunnable); // Detiene la actualización del gráfico
        }
    }

    private final Runnable updateChartRunnable = new Runnable() {
        @Override
        public void run() {
            short[] audioBuffer = new short[bufferSize / 2];
            if (audioRecord != null) {
                int readSize = audioRecord.read(audioBuffer, 0, audioBuffer.length);

                if (readSize > 0) {
                    processAudioData(audioBuffer, readSize);
                    if (dataSet.getEntryCount() > 0) { // Asegúrate de que haya datos antes de actualizar
                        lineData.notifyDataChanged();
                        chart.notifyDataSetChanged();
                        chart.invalidate();
                    }
                }

                handler.postDelayed(this, updateInterval);
            }
        }
    };

    private void processAudioData(short[] audioBuffer, int readSize) {
        for (int i = 0; i < readSize; i++) {
            float amplitude = Math.abs(audioBuffer[i]); // Calcula la amplitud del audio
            dataSet.addEntry(new Entry(currentIndex++, amplitude));
        }

        /*if (dataSet.getEntryCount() > 10) { // Limitar a 400 puntos en el gráfico
            dataSet.removeFirst();
        }*/
        // Eliminar puntos antiguos si se supera el máximo permitido
        while (dataSet.getEntryCount() > 10000) {
            dataSet.removeFirst();
        }


    }

}

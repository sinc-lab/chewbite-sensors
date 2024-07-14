package com.android.chewbiteSensors.data_sensors;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * La clase está diseñada para grabar audio desde el micrófono del dispositivo y guardarlo en un
 * archivo en formato 3gp. Proporciona métodos para iniciar y detener la grabación, así como para
 * administrar datos asociados (probablemente metadatos sobre la grabación).
 */
public enum AudioRecorder {
    // Evita que se cree más de una instancia de AudioRecorder
    INSTANCE;
    private MediaRecorder recorder = null;
    private ExperimentData data;
    // Formato del archivo de salida del audio
    private static final String outputFormat = "3gp";
    // establece la frecuencia de muestreo del audio
    private static final int samplingRate = 44100;
    // define la tasa de bits de audio
    private static final int bitRate = 128000;

    /**
     * Método para iniciar la grabación del audio.
     */
    public void start(Context context) {
        // Comprobar si Android es inferior a Android 10
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Genera un nombre de archivo único para el audio
            String fileName = String.format("audio.%s", outputFormat);
            File audioFile = FileManager.getPublicFile(data.getTimestamp(), fileName);
            // Inicializa una instancia de MediaRecorder y la configura:
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioEncodingBitRate(bitRate);
            recorder.setAudioSamplingRate(samplingRate);

            try {
                //
                recorder.prepare();
            } catch (IOException e) {
                Log.e("AudioRecord", "prepare() failed → " + e.getMessage());
            }
            recorder.start();

        }   else {
            String fileName = String.format("audio.%s", outputFormat);
            //File audioFile = new File(context.getExternalFilesDir(data.getTimestamp()), fileName);
            File audioFile = FileManager.getPublicFile(data.getTimestamp(), fileName);

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("AudioRecord", "prepare() failed → " + e.getMessage());
            }

            recorder.start();

        }

    }


    /**
     * Método para detener la grabación del audio. <br>
     * Libera los recursos de MediaRecorder.
     */
    public void stop() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public ExperimentData getExperimentData() {
        return data;
    }

    /**
     * Método para establecer datos asociados con la grabación del audio.
     */
    public void setExperimentData(ExperimentData data) {
        this.data = data;
    }
}

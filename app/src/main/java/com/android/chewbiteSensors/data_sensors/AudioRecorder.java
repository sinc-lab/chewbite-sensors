package com.android.chewbiteSensors.data_sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.settings.GetSettings;

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
    //private static final String outputFormat = "3gp";
    // establece la frecuencia de muestreo del audio
    //private static final int samplingRate = 44100;
    // define la tasa de bits de audio
    //private static final int bitRate = 128000;

    /**
     * Método para iniciar la grabación del audio.
     */
    public void start(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("status_controls", Context.MODE_PRIVATE);

        // Obtener el tipo de archivo, tasa de bits y frecuencia desde SharedPreferences
        int selectedFileTypePosition = sharedPreferences.getInt("status_switch_file_type_configuration", 0);
        int selectedBitRatePosition = sharedPreferences.getInt("status_switch_bit_rate_configuration", 0);
        int selectedFrequencyPosition = sharedPreferences.getInt("status_switch_frequency_sound_configuration", 0);

        // Convertir posiciones a valores reales
        String outputFormat = GetSettings.obtenerFormatoArchivo(context, selectedFileTypePosition);
        int bitRate = GetSettings.obtenerTasaBitRate(context, selectedBitRatePosition);
        int samplingRate = GetSettings.obtenerFrecuenciaMuestreo(context, selectedFrequencyPosition, R.array.text_frequency_sound_options);

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
            // Establece la frecuencia de muestreo de audio en muestras por segundo.
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
            // Establece la frecuencia de muestreo de audio en muestras por segundo.
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

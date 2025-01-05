package com.android.chewbiteSensors.data_sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
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

        // Genera un nombre de archivo único para el audio
        String fileName = String.format("audio.%s", outputFormat);
        File audioFile = FileManager.getFile(fileName);
        // Inicializa una instancia de MediaRecorder y la configura:
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        assert audioFile != null;
        recorder.setOutputFile(audioFile.getAbsolutePath());
        // Verifica la versión del SDK y configura el encoder adecuado
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.O) {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        }
        recorder.setAudioEncodingBitRate(bitRate);
        // Establece la frecuencia de muestreo de audio en muestras por segundo.
        recorder.setAudioSamplingRate(samplingRate);
        try {
            //
            recorder.prepare();
        } catch (IOException e) {
            Log.e("AudioRecord", "prepare() failed → " + e.getMessage());
        }
        try {
            recorder.start();
        } catch (Exception e) {
            Log.e("AudioRecord", "start() failed → " + e.getMessage());
        }
    }


    /**
     * Método para detener la grabación del audio. <br>
     * Libera los recursos de MediaRecorder.
     */
    public void stop() {
        /*recorder.stop();
        recorder.release();
        recorder = null;*/
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                Log.e("AudioRecorder", "Error stopping recorder: " + e.getMessage());
                // Manejar la excepción, por ejemplo, mostrar un mensaje al usuario
            }
            try {
                recorder.release();
            } catch (Exception e) {
                // Handle exceptions during stop or release
                e.printStackTrace(); // Or log the error appropriately
            } finally {
                recorder = null;
            }
        }
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

package com.android.chewbiteSensors.data_sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import com.android.chewbiteSensors.R;

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
        String outputFormat = obtenerFormatoArchivo(context, selectedFileTypePosition);
        int bitRate = obtenerTasaBitRate(context, selectedBitRatePosition);
        int samplingRate = obtenerFrecuenciaMuestreo(context, selectedFrequencyPosition);

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

    /*--------------------------------------------------------------------------------------------*/
    // Método para obtener el formato de archivo según la posición seleccionada
    private String obtenerFormatoArchivo(Context context, int position) {
        String[] opcionesFormatoArchivo = context.getResources().getStringArray(R.array.text_type_file_options);
        if (position >= 0 && position < opcionesFormatoArchivo.length) {
            return opcionesFormatoArchivo[position];
        } else {
            return "3gp"; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }

    // Método para obtener la tasa de bit rate según la posición seleccionada
    private int obtenerTasaBitRate(Context context, int position) {
        String[] opcionesBitRate = context.getResources().getStringArray(R.array.text_bit_rate_options);
        if (position >= 0 && position < opcionesBitRate.length) {
            // Convertimos el valor de String a entero
            return Integer.parseInt(opcionesBitRate[position].replace(".", ""));
        } else {
            return 128000; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }

    // Método para obtener la frecuencia de muestreo según la posición seleccionada
    private int obtenerFrecuenciaMuestreo(Context context, int position) {
        String[] opcionesFrecuencia = context.getResources().getStringArray(R.array.text_frequency_options);
        if (position >= 0 && position < opcionesFrecuencia.length) {
            // Convertimos el valor de String a entero
            return Integer.parseInt(opcionesFrecuencia[position].replace(".", ""));
        } else {
            return 44100; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }
    /*--------------------------------------------------------------------------------------------*/
}

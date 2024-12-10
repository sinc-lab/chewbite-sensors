package com.android.chewbiteSensors.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.chewbiteSensors.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetSettings {
    // Atributo
    private static final String PREFS_KEY = "status_controls";
    private static final String NAME_OF_THE_EXPERIMENT = "name_of_the_experiment";
    private static final String DATE_FORMAT = "yyyy_MM_dd-HH_mm_ss";

    /*--------------------------------------------------------------------------------------------*/
    // Método para obtener el formato de archivo según la posición seleccionada
    public static String obtenerFormatoArchivo(Context context, int position) {
        String[] opcionesFormatoArchivo = context.getResources().getStringArray(R.array.text_type_file_options);
        if (position >= 0 && position < opcionesFormatoArchivo.length) {
            return opcionesFormatoArchivo[position];
        } else {
            return "3gp"; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }

    // Método para obtener la tasa de bit rate según la posición seleccionada
    public static int obtenerTasaBitRate(Context context, int position) {
        String[] opcionesBitRate = context.getResources().getStringArray(R.array.text_bit_rate_options);
        if (position >= 0 && position < opcionesBitRate.length) {
            // Convertimos el valor de String a entero
            return Integer.parseInt(opcionesBitRate[position].replace(".", ""));
        } else {
            return 128000; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }

    // Método para obtener la frecuencia de muestreo según la posición seleccionada
    public static int obtenerFrecuenciaMuestreo(Context context, int position, int frequencyOptionsArray) {
        String[] opcionesFrecuencia = context.getResources().getStringArray(frequencyOptionsArray);
        if (position >= 0 && position < opcionesFrecuencia.length) {
            // Convertimos el valor de String a entero
            return Integer.parseInt(opcionesFrecuencia[position].replace(".", ""));
        } else {
            return 44100; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }

    // Método para obtener la frecuencia de muestreo según la posición seleccionada
    /*public static double obtenerFrecuenciaMuestreoGPS(Context context, int position, int frequencyOptionsArray) {
        String[] opcionesFrecuencia = context.getResources().getStringArray(frequencyOptionsArray);
        if (position >= 0 && position < opcionesFrecuencia.length) {
            // Saca el punto que divide los miles
            String sacarPuntoSeparadorDeMiles = opcionesFrecuencia[position].replace(".", "");
            // Cambia la coma por el punto y lo convierte a double
            return Double.parseDouble(sacarPuntoSeparadorDeMiles.replace(",", "."));
        } else {
            return 1; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }*/

    /*--------------------------------------------------------------------------------------------*/
    @SuppressLint("SimpleDateFormat")
    public static String getExperimentName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        String nombreDelExperimento = sharedPreferences.getString(NAME_OF_THE_EXPERIMENT, "");
        if (nombreDelExperimento.isEmpty()) {
            Date fecha = new Date();
            nombreDelExperimento = new SimpleDateFormat(DATE_FORMAT).format(fecha);
        }
        return nombreDelExperimento;
    }

    public static void setExperimentName(Context context, String nameExperiment) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(NAME_OF_THE_EXPERIMENT, nameExperiment).apply();
    }
    /*--------------------------------------------------------------------------------------------*/
}

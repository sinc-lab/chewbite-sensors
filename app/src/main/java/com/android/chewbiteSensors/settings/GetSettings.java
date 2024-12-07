package com.android.chewbiteSensors.settings;

import android.content.Context;

import com.android.chewbiteSensors.R;

public class GetSettings {

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
    public static double obtenerFrecuenciaMuestreoGPS(Context context, int position, int frequencyOptionsArray) {
        String[] opcionesFrecuencia = context.getResources().getStringArray(frequencyOptionsArray);
        if (position >= 0 && position < opcionesFrecuencia.length) {
            // Saca el punto que divide los miles
            String sacarPuntoSeparadorDeMiles = opcionesFrecuencia[position].replace(".", "");
            // Cambia la coma por el punto y lo convierte a double
            return Double.parseDouble(sacarPuntoSeparadorDeMiles.replace(",", "."));
        } else {
            return 1; // Valor por defecto en caso de que la posición esté fuera de rango
        }
    }
    /*--------------------------------------------------------------------------------------------*/
}

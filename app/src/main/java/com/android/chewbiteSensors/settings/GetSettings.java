package com.android.chewbiteSensors.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.chewbiteSensors.R;

public class GetSettings {
    // Atributo
    private static final String PREFS_KEY = "status_controls";
    private static final String NAME_OF_THE_EXPERIMENT = "name_of_the_experiment";
    private static final String STATUS_SWT_SOUND_CONFIG = "status_switch_sound_configuration";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";
    private static final String STATUS_SWT_GPS_CONFIG = "status_switch_gps_configuration";
    private static final String STATUS_SWT_ACCELEROMETER_CONFIG = "status_switch_accelerometer_configuration";
    private static final String STATUS_SWT_GYROSCOPE_CONFIG = "status_switch_gyroscope_configuration";
    private static final String STATUS_SWT_MAGNETOMETER_CONFIG = "status_switch_magnetometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG = "status_switch_uncalibrated_accelerometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG = "status_switch_uncalibrated_gyroscope_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG = "status_switch_uncalibrated_magnetometer_configuration";
    private static final String STATUS_SWT_GRAVITY_CONFIG = "status_switch_gravity_configuration";
    private static final String STATUS_SWT_NUMBER_OF_STEPS_CONFIG = "status_switch_number_of_steps_configuration";
    private static final String STATUS_SPN_BIT_RATE_CONFIG = "status_switch_bit_rate_configuration";
    private static final String STATUS_SPN_FREQUENCY_CONFIG = "status_switch_frequency_sound_configuration";
    private static final String STATUS_SPN_FILE_TYPE_CONFIG = "status_switch_file_type_configuration";
    private static final String STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG = "status_spinner_frequency_movement_configuration";
    private static final String STATUS_SPN_FREQUENCY_GPS_CONFIG = "status_switch_frequency_gps_configuration";


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
        String nombreDelExperimento = sharedPreferences.getString(NAME_OF_THE_EXPERIMENT, "Test");
        if (nombreDelExperimento.trim().isEmpty()) {
            /*Date fecha = new Date();
            nombreDelExperimento = new SimpleDateFormat(DATE_FORMAT).format(fecha);*/
            nombreDelExperimento = "Test";
        }
        return nombreDelExperimento;
    }

    public static void setExperimentName(Context context, String nameExperiment) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        nameExperiment = (nameExperiment == null || nameExperiment.trim().isEmpty()) ? "Test" : nameExperiment;
        sharedPreferences.edit().putString(NAME_OF_THE_EXPERIMENT, nameExperiment).apply();
    }

    /*--------------------------------------------------------------------------------------------*/

    /**
     * Obtiene el estado de los switch
     *
     * @param switchName Opciones disponibles: sound, accelerometer, gyroscope, magnetometer,
     *                   uncalibrated_accelerometer, uncalibrated_gyroscope,
     *                   uncalibrated_magnetometer, gravity, number_of_steps, gps, movement
     * @param context    Context de la actividad
     * @return true si el switch está activado, false en caso contrario
     */
    public static boolean getStatusSwitch(String switchName, Context context) {
        // Obtener el estado del switch desde SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        switch (switchName) {
            case "sound":
                return sharedPreferences.getBoolean(STATUS_SWT_SOUND_CONFIG, true);
            case "accelerometer":
                return sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
            case "gyroscope":
                return sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
            case "magnetometer":
                return sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
            case "uncalibrated_accelerometer":
                return sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, false);
            case "uncalibrated_gyroscope":
                return sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, false);
            case "uncalibrated_magnetometer":
                return sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, false);
            case "gravity":
                return sharedPreferences.getBoolean(STATUS_SWT_GRAVITY_CONFIG, false);
            case "number_of_steps":
                return sharedPreferences.getBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, false);
            case "gps":
                return sharedPreferences.getBoolean(STATUS_SWT_GPS_CONFIG, false);
            case "movement":

                boolean accelerometerStatus = getStatusSwitch("accelerometer", context);
                boolean gyroscopeStatus = getStatusSwitch("gyroscope", context);
                boolean magnetometerStatus = getStatusSwitch("magnetometer", context);
                boolean accelerometerUncalibratedStatus = getStatusSwitch("uncalibrated_accelerometer", context);
                boolean gyroscopeUncalibratedStatus = getStatusSwitch("uncalibrated_gyroscope", context);
                boolean magnetometerUncalibratedStatus = getStatusSwitch("uncalibrated_magnetometer", context);
                boolean gravityStatus = getStatusSwitch("gravity", context);
                boolean numberOfStepsStatus = getStatusSwitch("number_of_steps", context);

                boolean movementStatus = sharedPreferences.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);

                return (accelerometerStatus || gyroscopeStatus || magnetometerStatus ||
                        accelerometerUncalibratedStatus || gyroscopeUncalibratedStatus ||
                        magnetometerUncalibratedStatus || gravityStatus || numberOfStepsStatus) && movementStatus;
            default:
                return false;
        }
    }
    /*--------------------------------------------------------------------------------------------*/

    /**
     * Guarda el estado de los switch
     *
     * @param switchName Opciones disponibles: sound, accelerometer, gyroscope, magnetometer,
     *                   uncalibrated_accelerometer, uncalibrated_gyroscope,
     *                   uncalibrated_magnetometer, gravity, number_of_steps, gps, movement
     * @param status     Estado del switch
     * @param context    Context de la app
     */
    public static void setStatusSwitch(String switchName, boolean status, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        // Obtener el estado del switch desde SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (switchName) {
            case "sound":
                editor.putBoolean(STATUS_SWT_SOUND_CONFIG, status);
                break;
            case "accelerometer":
                editor.putBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, status);
                break;
            case "gyroscope":
                editor.putBoolean(STATUS_SWT_GYROSCOPE_CONFIG, status);
                break;
            case "magnetometer":
                editor.putBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, status);
                break;
            case "uncalibrated_accelerometer":
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, status);
                break;
            case "uncalibrated_gyroscope":
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, status);
                break;
            case "uncalibrated_magnetometer":
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, status);
                break;
            case "gravity":
                editor.putBoolean(STATUS_SWT_GRAVITY_CONFIG, status);
                break;
            case "number_of_steps":
                editor.putBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, status);
                break;
            case "gps":
                editor.putBoolean(STATUS_SWT_GPS_CONFIG, status);
                break;
            case "movement":
                editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, status);
                break;
        }
        editor.apply();
    }
    /*--------------------------------------------------------------------------------------------*/

    /**
     * Obtiene el estado del Spinner
     *
     * @param SpinnerName Opciones disponibles: bit_rate_sound, frecuency_sound, file_type,
     *                    frecuency_movement, frecuency_gps
     * @param context     Context de la app
     * @return número de posición en el array de opciones disponibles
     */
    public static int getStatusSpinner(String SpinnerName, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        switch (SpinnerName) {
            case "bit_rate_sound":
                return sharedPreferences.getInt(STATUS_SPN_BIT_RATE_CONFIG, 0);
            case "frecuency_sound":
                return sharedPreferences.getInt(STATUS_SPN_FREQUENCY_CONFIG, 0);
            case "file_type":
                return sharedPreferences.getInt(STATUS_SPN_FILE_TYPE_CONFIG, 0);
            case "frecuency_movement":
                return sharedPreferences.getInt(STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG, 0);
            case "frecuency_gps":
                return sharedPreferences.getInt(STATUS_SPN_FREQUENCY_GPS_CONFIG, 0);
            default:
                return 0;
        }
    }
    /*--------------------------------------------------------------------------------------------*/

    /**
     * Guarda el estado del Spinner
     *
     * @param SpinnerName Opciones disponibles: bit_rate_sound, frecuency_sound, file_type,
     *                    frecuency_movement, frecuency_gps
     * @param position    Posición que se seleccionó
     * @param context     Context de la app
     */
    public static void setStatusSpinner(String SpinnerName, int position, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        // Obtener el estado del switch desde SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (SpinnerName) {
            case "bit_rate_sound":
                editor.putInt(STATUS_SPN_BIT_RATE_CONFIG, position);
                break;
            case "frecuency_sound":
                editor.putInt(STATUS_SPN_FREQUENCY_CONFIG, position);
                break;
            case "file_type":
                editor.putInt(STATUS_SPN_FILE_TYPE_CONFIG, position);
                break;
            case "frecuency_movement":
                editor.putInt(STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG, position);
                break;
            case "frecuency_gps":
                editor.putInt(STATUS_SPN_FREQUENCY_GPS_CONFIG, position);
                break;
        }
        editor.apply();
    }
    /*--------------------------------------------------------------------------------------------*/
}

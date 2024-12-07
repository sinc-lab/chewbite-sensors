package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.POWER_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.settings.GetSettings;

import java.util.Timer;

public enum CBGPSListener implements LocationListener {
    INSTANCE;

    private HandlerThread mGPSThread;
    private Handler mGPSHandler;
    private static final String PREFS_KEY = "status_controls";
    private static final String STATUS_SPN_FREQUENCY_GPS_CONFIG = "status_switch_frequency_gps_configuration";
    private LocationManager locationManager;
    private Timer timer;
    private ExperimentData data;
    private boolean isRunning = false;
    private PowerManager.WakeLock wakeLock;
    private double samplingRateGps;
    private CBGpsBuffer sensor = new CBGpsBuffer(CBBuffer.STRING_GPS);

    /**
     * Establece los datos del experimento.
     * @param data Los datos del experimento.
     */
    public void setExperimentData(ExperimentData data) {
        this.data = data;
    }


    // API de GOOGLE
    // No se usa porque son pagas
    /*public void start(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L, // minimum time interval between location updates in milliseconds
                    0F, // minimum distance between location updates in meters
                    CBGPSListener.INSTANCE // Pass the INSTANCE of your enum
            );
            isRunning = true;
            // Schedule periodic data recording
            scheduleDataRecording();
        } catch (SecurityException e) {
            // Handle permission issues
            e.printStackTrace();
        }
    }*/

    /**
     * Inicia la recolección de datos GPS.
     * @param context El contexto de la aplicación.
     */
    public void startNativeGPS(Context context) {
        // Obtiene un WakeLock para evitar que el dispositivo entre en suspensión durante la
        // recolección de datos, asegurando que el proceso continúe sin interrupciones.
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CB::MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
        // Inicia la recolección de datos GPS
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        // Frecuencia de muestreo
        int selectedFrequencyPositionGPS = sharedPreferences.getInt(STATUS_SPN_FREQUENCY_GPS_CONFIG, 0);
        this.samplingRateGps = GetSettings.obtenerFrecuenciaMuestreoGPS(context, selectedFrequencyPositionGPS, R.array.text_frequency_gps_options);
        // Convierte la frecuencia de muestreo a milisegundos
        long samplingInterval = (long) (1000L / this.samplingRateGps);

        try {
            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException("Permissions not granted");
            }

            // Inicia el hilo para la recolección de datos GPS
            mGPSThread = new HandlerThread("GPS thread", Thread.MAX_PRIORITY);
            mGPSThread.start();
            mGPSHandler = new Handler(mGPSThread.getLooper());
            // 5-)
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,   // Indica que queremos obtener las ubicaciones del GPS del dispositivo
                    samplingInterval,               // Intervalo de tiempo mínimo entre actualizaciones (en milisegundos). Ejemplo: Si samplingInterval = 1000, el GPS emitirá actualizaciones como máximo una vez por segundo.
                    0,                              // Distancia mínima entre actualizaciones (en metros). Aquí, si el dispositivo no se ha movido al menos 1 metro, no se enviará una nueva ubicación.
                    this,                           // Indica que la clase actual (CBGPSListener) actuará como LocationListener para recibir las actualizaciones de ubicación.
                    mGPSHandler.getLooper()         // El listener (this) ejecutará las actualizaciones en un hilo específico (HandlerThread llamado mGPSThread), en lugar del hilo principal (UI thread). Esto evita bloquear la interfaz de usuario mientras se procesan las actualizaciones.
            );
            isRunning = true;
            // 6-)
            this.scheduleDataRecording(data.getTimestamp());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene la recolección de datos GPS.
     */
    public void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            isRunning = false;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        // Libera el HandlerThread
        if (mGPSThread != null) {
            mGPSThread.quitSafely();
            mGPSThread = null;
        }
    }
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Maneja la actualización de la ubicación.
     * @param location the updated location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //currentLocation = location;
        try {
            sensor.append(new SensorEventData(location.getTime(), location.getLatitude(), location.getLongitude(), location.getAltitude()));
        } catch (Exception e) {
            FileManager.writeToFile(data.getTimestamp(), "error.txt", e.getMessage() + '\n');
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Gestionar la desactivación del proveedor de GPS
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Manejar la habilitación del proveedor GPS
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Gestionar cambios de estado del proveedor de GPS
    }

    /**
     * Programa la grabación de datos GPS.
     * @param directoryName El nombre del directorio donde se guardarán los datos.
     */
    private void scheduleDataRecording(String directoryName) {
        // Crear una instancia de CBGpsBuffer
        //CBGpsBuffer sensor = new CBGpsBuffer(CBBuffer.STRING_GPS);
        // Asignar los datos del experimento al sensor
        sensor.setFileNumber(this.data.getFileNumber());

        WriteFileTaskGPS<CBGpsBuffer> writeFileTaskGps = new WriteFileTaskGPS<>();

        writeFileTaskGps.addSensor(sensor);
        // Setear el nombre del directorio
        writeFileTaskGps.setDirectoryName(directoryName);

        timer = new Timer();
        timer.schedule(writeFileTaskGps, WriteFileTaskGPS.DELAY_TIME_MS, WriteFileTaskGPS.PERIOD_TIME_MS);
        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentLocation != null) {
                    // Registrar datos GPS (latitud, longitud, altitud, marca de tiempo)
                    // en su objeto ExperimentData u otro mecanismo de almacenamiento.
                    // ... Acceder a los datos usando currentLocation.getLatitude(), etc. ...
                    double latitude = currentLocation.getLatitude();
                    double longitude = currentLocation.getLongitude();
                    double altitude = currentLocation.getAltitude();
                    long timestamp = currentLocation.getTime();

                    try {
                        sensor.append(new SensorEventData(timestamp, latitude, longitude, altitude));
                    } catch (Exception e) {
                        FileManager.writeToFile(data.getTimestamp(), "error.txt", e.getMessage() + '\n');
                    }
                }
            }
        }, WriteFileTaskGPS.DELAY_TIME_MS, WriteFileTaskGPS.PERIOD_TIME_MS); ///Grabar cada 1000 milisegundos (1 segundo)
         */
    }
}
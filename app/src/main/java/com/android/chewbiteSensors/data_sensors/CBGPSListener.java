package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.POWER_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.Process;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.settings.GetSettings;

import java.util.Timer;
import java.util.TimerTask;

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
    private Context context;
    private CBGpsBuffer sensor = new CBGpsBuffer(CBBuffer.STRING_GPS);
    // Al nivel de la clase, junto a las demás variables
    private volatile Location lastReceivedLocation;

    /**
     * Establece los datos del experimento.
     *
     * @param data Los datos del experimento.
     */
    public void setExperimentData(ExperimentData data) {
        this.data = data;
    }

    /**
     * Establece el número de archivo del experimento.
     */
    public void changeFileNumber() {
        // Establece el número de archivo del sensor
        sensor.setFileNumber(this.data.getFileNumber());
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
     *
     * @param context El contexto de la aplicación.
     */
    public void startNativeGPS(Context context) {
        this.context = context;

        // limpia la variable
        lastReceivedLocation = null;

        // Obtiene un WakeLock para evitar que el dispositivo entre en suspensión durante la
        // recolección de datos, asegurando que el proceso continúe sin interrupciones.
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CB::MyWakelockTag");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        // Inicia la recolección de datos GPS
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        // Frecuencia de muestreo (seg)
        int selectedFrequencyPositionGPS = sharedPreferences.getInt(STATUS_SPN_FREQUENCY_GPS_CONFIG, 0);
        this.samplingRateGps = GetSettings.obtenerFrecuenciaMuestreo(context, selectedFrequencyPositionGPS, R.array.text_frequency_gps_options);
        // Convierte el tiempo entre muestras de segundos a milisegundos
        //long samplingInterval = (long) (1000L * this.samplingRateGps);
        // milisegundos fijos para mínimos requeridos
        long samplingInterval = 1000L;

        try {
            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException("Permissions not granted");
            }

            // Inicia el hilo para la recolección de datos GPS
            mGPSThread = new HandlerThread("GPS thread", Process.THREAD_PRIORITY_DISPLAY);
            mGPSThread.start();
            /*  Establecer la prioridad a nivel Java:
                - NORM_PRIORITY = 5
                - MAX_PRIORITY = 10
             */
            mGPSThread.setPriority(8); // Esto establece el valor a 8
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
            this.scheduleDataRecording();

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
     *
     * @param location the updated location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //currentLocation = location;
        try {
            //sensor.append(new SensorEventData(location.getTime(), location.getLatitude(), location.getLongitude(), location.getAltitude()));
            // Solo se almacena el último valor recibido para ser muestreado luego
            lastReceivedLocation = location;
        } catch (Exception e) {
            //FileManager.writeToFile(data.getTimestamp(), "error.txt", e.getMessage() + '\n');
            FileManager.writeToFile("error.txt", e.getMessage() + '\n');
        }
    }

    /**
     * Maneja el deshabilitamiento del proveedor de ubicación.
     * Este método se llama cuando el proveedor de ubicación se desactiva, es decir, por ejemplo:
     * se presiona el icono de deshabilitar en la ubicación GPS en el dispositivo.
     *
     * @param provider el nombre del proveedor de ubicación
     */
    @Override
    public void onProviderDisabled(String provider) {
        // 1. Stop location updates:
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            isRunning = false;
        }
        // 2. Opcionalmente, notifique al usuario:
        // Puede mostrar un mensaje de notificación o actualizar la interfaz de usuario para informar al usuario
        // que el proveedor de GPS se ha deshabilitado. Esto es útil para brindar
        // retroalimentación y guiarlos para habilitar el GPS si es necesario.
        Toast.makeText(this.context, "GPS desactivado", Toast.LENGTH_SHORT).show();

        // 3. Opcionalmente, intente volver a habilitar el GPS:
        // Puede considerar solicitarle al usuario que habilite el GPS o redirigirlo
        // automáticamente a la configuración de ubicación para volver a activarlo.
        // Este es un enfoque más proactivo, pero requiere un manejo cuidadoso
        // de los permisos y preferencias del usuario.
        // Esta redirección a la configuración de ubicación no se va a usar en este caso.
        //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //this.context.startActivity(intent);
    }

    /* por el momento no se usa
    @Override
    public void onProviderEnabled(String provider) {
        // Manejar la habilitación del proveedor GPS
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Gestionar cambios de estado del proveedor de GPS
    }
    */

    /**
     * Programa la grabación de datos GPS.
     *
     */
    /*private void scheduleDataRecording() {
        // Crear una instancia de CBGpsBuffer
        //CBGpsBuffer sensor = new CBGpsBuffer(CBBuffer.STRING_GPS);
        // Asignar los datos del experimento al sensor
        sensor.setFileNumber(this.data.getFileNumber());

        WriteFileTaskGPS<CBGpsBuffer> writeFileTaskGps = new WriteFileTaskGPS<>();

        writeFileTaskGps.addSensor(sensor);
        // Setear el nombre del directorio
        //writeFileTaskGps.setDirectoryName(directoryName);

        timer = new Timer();
        //timer.schedule(writeFileTaskGps, WriteFileTaskGPS.DELAY_TIME_MS, WriteFileTaskGPS.PERIOD_TIME_MS);
        timer.schedule(writeFileTaskGps, WriteFileTaskGPS.DELAY_TIME_MS, samplingIntervalMs);
    }*/

    /**
     * Programa el muestreo exacto de datos GPS.
     */
    private void scheduleDataRecording() {
        // Se asegura de que el sensor utilice el número de archivo correcto
        sensor.setFileNumber(this.data.getFileNumber());

        // Se crea un Timer para programar el muestreo a intervalos fijos
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        // Se convierte la frecuencia de muestreo (en segundos) a milisegundos.
        final long samplingIntervalMs = (long) (this.samplingRateGps * 1000L);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Se toma la hora actual; puedes elegir entre System.currentTimeMillis() o el
                // timestamp del dato GPS. Aquí uso el tiempo del muestreo para garantizar exactitud.
                //long sampleTimestamp = System.currentTimeMillis();

                // Se verifica que se tenga un dato GPS (en caso de que todavía no haya llegado ninguno)
                if (lastReceivedLocation != null) {
                    long timestamp = lastReceivedLocation.getTime();
                    // Se utiliza la última ubicación recibida
                    double latitude = lastReceivedLocation.getLatitude();
                    double longitude = lastReceivedLocation.getLongitude();
                    double altitude = lastReceivedLocation.getAltitude();

                    // Se crea un nuevo objeto SensorEventData (asegúrate que su constructor o método acepte estos datos)
                    SensorEventData sample = new SensorEventData(timestamp, latitude, longitude, altitude);

                    // Se guarda la muestra en el buffer
                    try {
                        sensor.append(sample);
                    } catch (Exception e) {
                        FileManager.writeToFile("error.txt", e.getMessage() + '\n');
                    }

                    // Escribimos el contenido acumulado del sensor a disco
                    // (Si prefieres escribir solo la muestra actual, podrías modificar esto para no reescribir todo el buffer)
                    FileManager.writeToFile(sensor.getSensorFileName(), sensor.getSensorEventData(CBBuffer.STRING_GPS));
                    // limpia la variable
                    lastReceivedLocation = null;
                }
            }
        }, samplingIntervalMs, samplingIntervalMs);
    }
}
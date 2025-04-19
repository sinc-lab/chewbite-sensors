package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.POWER_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.settings.GetSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Es el corazón de la recopilación y gestión de datos de sus sensores en el proyecto "chewBite Sensors".
 * Implementa la interfaz SensorEventListener, lo que significa que es responsable de manejar los
 * eventos del sensor desde el dispositivo Android.
 */
public enum CBSensorEventListener implements SensorEventListener {
    INSTANCE;

    private SensorManager sensorManager;

    private Timer timer;

    private List<CBSensorBuffer> sensors;

    private ExperimentData data;

    private SensorEventListenerState state = SensorEventListenerState.IDLE;

    private HandlerThread mSensorThread;

    PowerManager.WakeLock wakeLock;
    private static final String PREFS_KEY = "status_controls";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";
    private static final String STATUS_SWT_ACCELEROMETER_CONFIG = "status_switch_accelerometer_configuration";
    private static final String STATUS_SWT_GYROSCOPE_CONFIG = "status_switch_gyroscope_configuration";
    private static final String STATUS_SWT_MAGNETOMETER_CONFIG = "status_switch_magnetometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG = "status_switch_uncalibrated_accelerometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG = "status_switch_uncalibrated_gyroscope_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG = "status_switch_uncalibrated_magnetometer_configuration";
    private static final String STATUS_SWT_GRAVITY_CONFIG = "status_switch_gravity_configuration";
    private static final String STATUS_SWT_NUMBER_OF_STEPS_CONFIG = "status_switch_number_of_steps_configuration";
    private static final String STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG = "status_spinner_frequency_movement_configuration";
    private int samplingRate;
    private int samplingPeriodUs;
    // Cola para encolar eventos de sensor de forma thread-safe
    private final ConcurrentLinkedQueue<SensorEventCopy> sensorEventQueue = new ConcurrentLinkedQueue<>();
    private Handler mSensorHandler;
    private long bootOffset; // Offset para convertir el timestamp del sensor a tiempo real (ms)
    // Variables de instancia
    private long currentSecond = -1;
    private final Map<Integer, List<SensorEventCopy>> pendingEvents = new HashMap<>();

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
        this.data.setFileNumber(this.data.getFileNumber() + 1);
        // Establece el número de archivo de los sensores
        for (CBSensorBuffer sensor : this.sensors) {
            // Establece el número de archivo del sensor
            sensor.setFileNumber(this.data.getFileNumber());
        }
    }

    /**
     * El método start es el encargado de iniciar la recolección de datos de los sensores en el experimento.
     * Se encarga de configurar los sensores que se van a utilizar,
     * la frecuencia de muestreo y otros parámetros,
     * para luego registrarlos y comenzar a guardar los datos.
     *
     * @param context El contexto de la aplicación.
     */
    public void start(Context context) throws SecurityException {
        // ——— 0) Limpieza de estado previo ———
        this.clearVariables();

        // Obtiene un WakeLock para evitar que el dispositivo entre en suspensión durante la
        // recolección de datos, asegurando que el proceso continúe sin interrupciones.
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CB::MyWakelockTag");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        // Establece el estado del sensor
        this.state = SensorEventListenerState.RUNNING;
        // Obtiene el servicio de sensores
        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        // Crea una lista de sensores
        this.sensors = new ArrayList<>();
        // En estas lineas es donde se pueden agregar los sensores
        // Se agrega el acelerómetro, el giroscopio y el campo magnético

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        boolean movementStatus = sharedPreferences.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
        boolean accelerometerStatus = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
        boolean gyroscopeStatus = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
        boolean magnetometerStatus = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
        boolean accelerometerUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, false);
        boolean gyroscopeUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, false);
        boolean magnetometerUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, false);
        boolean gravityStatus = sharedPreferences.getBoolean(STATUS_SWT_GRAVITY_CONFIG, false);
        boolean numberOfStepsStatus = sharedPreferences.getBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, false);
        // Frecuencia de muestreo
        int selectedFrequencyPosition = sharedPreferences.getInt(STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG, 0);
        this.samplingRate = GetSettings.obtenerFrecuenciaMuestreo(context, selectedFrequencyPosition, R.array.text_frequency_movement_options);
        // Convertir la frecuencia a microsegundos
        this.samplingPeriodUs = (int) ((1.0 / samplingRate) * 1_000_000); // Convierte Hz a us
        Log.d("CBSensorEventListener", "Frecuencia: " + samplingRate + " Hz, Período: " + samplingPeriodUs + " µs");

        if (movementStatus) {
            if (accelerometerStatus) {
                this.addSensor(CBBuffer.STRING_ACCELEROMETER, Sensor.TYPE_ACCELEROMETER);
            }
            if (gyroscopeStatus) {
                this.addSensor(CBBuffer.STRING_GYROSCOPE, Sensor.TYPE_GYROSCOPE);
            }
            if (magnetometerStatus) {
                this.addSensor(CBBuffer.STRING_MAGNETIC_FIELD, Sensor.TYPE_MAGNETIC_FIELD);
            }
            /*------------------------------------------------------------------------*/
            if (accelerometerUncalibratedStatus) {
                this.addSensor(CBBuffer.STRING_ACCELEROMETER_UNCALIBRATED, Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
            }
            if (gyroscopeUncalibratedStatus) {
                this.addSensor(CBBuffer.STRING_GYROSCOPE_UNCALIBRATED, Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
            }
            if (magnetometerUncalibratedStatus) {
                this.addSensor(CBBuffer.STRING_MAGNETIC_FIELD_UNCALIBRATED, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
            }
            if (gravityStatus) {
                this.addSensor(CBBuffer.STRING_GRAVITY, Sensor.TYPE_GRAVITY);
            }
            if (numberOfStepsStatus) {
                this.addSensor(CBBuffer.STRING_NUM_OF_STEPS, Sensor.TYPE_STEP_COUNTER);
            }
            /*------------------------------------------------------------------------*/
        }
        // Calcular el offset entre System.currentTimeMillis() y SystemClock.elapsedRealtime()
        bootOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        Log.d("CBSensorEventListener", "Boot offset: " + bootOffset +
                "\nSystem.currentTimeMillis(): " + System.currentTimeMillis() +
                "\nSystemClock.elapsedRealtime(): " + SystemClock.elapsedRealtime());
        // 5-) Registra los sensores
        this.registerDeviceSensors();
        // En lugar de procesar evento a evento, se lanza el procesamiento por lotes cada segundo
        mSensorHandler.post(processBatchRunnable);
        // 6-) Guarda los datos
        this.scheduleWriteFile();
    }

    /**
     * Finaliza el experimento.
     */
    public void stop() {
        this.cancelTimer();
        this.unregisterDeviceSensors();
        this.state = SensorEventListenerState.IDLE;

        wakeLock.release();

        // Limpieza al parar
        this.clearVariables();
    }

    private void clearVariables() {
        this.currentSecond = -1;
        this.sensorEventQueue.clear();
        this.pendingEvents.clear();
    }

    /**
     * Indica si el experimento está corriendo.
     *
     * @return true si el experimento está corriendo, false en caso contrario.
     */
    public boolean isRunning() {
        return this.state == SensorEventListenerState.RUNNING;
    }

    /*
     * Se llama cuando un sensor cambia de valor.
     *
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    /*@Override
    public void onSensorChanged(SensorEvent event) {
        int event_sensor_Type = event.sensor.getType();
        long event_timestamp = event.timestamp;
        float[] event_values = event.values;

        try {
            if (sensors.stream().noneMatch(s -> s.getSensor().getType() == event_sensor_Type) ) {
                // No existe ningún sensor con este eventType
                return;
            }
            // Busca el sensor correspondiente al tipo de evento
            CBSensorBuffer sensor = sensors.stream()
                    .filter(s -> s.getSensor().getType() == event_sensor_Type)
                    .findFirst()
                    .orElse(null);

            if (event_sensor_Type == Sensor.TYPE_STEP_COUNTER) {
                int stepCount = (int) event_values[0];  // Confirma si este cast es seguro
                // Si es la primera lectura, guarda el valor inicial
                assert sensor != null; // Asegúrate de que sensor no sea nulo
                if (sensor.getInitialStepCount() == -1) {
                    sensor.setInitialStepCount(stepCount);
                }
                // Ajusta el valor restando el valor inicial
                stepCount -= sensor.getInitialStepCount();
                // Agrega los datos ajustados al buffer
                sensor.append(new SensorEventData(event_timestamp, stepCount));
            } else {
                // Otros sensores
                assert sensor != null;
                sensor.append(new SensorEventData(event_timestamp, event_values[0], event_values[1], event_values[2]));
            }

        } catch (Exception e) {
            FileManager.writeToFile("error.txt", e.getMessage() + "\n");
        }
    }*/

    /*------------------------------- Cambios 1---------------------------------------------------*/
    /*@Override
    public void onSensorChanged(SensorEvent event) {
        sensorEventQueue.offer(event);
        Log.d("CBSensorEventListener", "Evento encolado: " + event.timestamp);
    }*/
    /*------------------------------- Cambios 1---------------------------------------------------*/
    /*------------------------------- Cambios 2---------------------------------------------------*/
    /*@Override
    public void onSensorChanged(SensorEvent event) {
        // Crear una copia de los datos del evento
        SensorEventCopy copy = new SensorEventCopy(
                event.timestamp,
                event.values,
                event.sensor.getType()
        );
        sensorEventQueue.offer(copy);
        //Log.d("CBSensorEventListener", "sensorEventQueue.offer(copy) size: " + sensorEventQueue.size());
    }*/
    /*------------------------------- Cambios 2---------------------------------------------------*/
    /*------------------------------- Cambios 3---------------------------------------------------*/
    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorEventQueue.offer(new SensorEventCopy(event));
    }
    /*------------------------------- Cambios 3---------------------------------------------------*/

    /**
     * Se llama cuando la precisión de un sensor ha cambiado.
     *
     * @param arg0 El sensor que ha cambiado de precisión.
     * @param arg1 La nueva precisión de este sensor, uno de
     *             {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    /**
     * Cancela el temporizador de escritura de archivos.
     */
    public void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private void addSensor(String sensorName, int sensorType) {
        Sensor sensor = this.sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            CBSensorBuffer sensorBuffer = new CBSensorBuffer(sensorName, sensor);
            sensorBuffer.setFileNumber(this.data.getFileNumber());
            this.sensors.add(sensorBuffer);
        }
    }

    /**
     * Registra los sensores en el dispositivo.
     */
    private void registerDeviceSensors() {
        /* Parámetros:
            - tid: Identificador del hilo/proceso que se va a modificar.
            - priority: Nivel de prioridad de Linux, desde -20 para la máxima prioridad de
            programación hasta 19 para la mínima.
         */
        //mSensorThread = new HandlerThread("Sensor thread", Process.THREAD_PRIORITY_URGENT_DISPLAY);
        mSensorThread = new HandlerThread("Sensor thread", -18);
        // Se crea un hilo de sensor con Thread.MAX_PRIORITY (10)
        // y en onLooperPrepared se cambiará a THREAD_PRIORITY_URGENT_DISPLAY
        //mSensorThread = new SensorHandlerThread("Sensor thread", Process.THREAD_PRIORITY_URGENT_DISPLAY);
        mSensorThread.start();
        // Establecer la máxima prioridad a nivel Java:
        mSensorThread.setPriority(Thread.MAX_PRIORITY); // Esto establece el valor a 10
        mSensorHandler = new Handler(mSensorThread.getLooper()); //Blocks until looper is prepared, which is fairly quick

        for (CBSensorBuffer sensor : sensors) {
            //String name = sensor.getSensorName();
            //int max = sensor.getSensor().getMaxDelay();
            //this.sensorManager.registerListener(this, sensor.getSensor(), this.samplingPeriodUs, 1000000, mSensorHandler);
            this.sensorManager.registerListener(
                    this,
                    sensor.getSensor(),
                    0,      // En microsegundos (la API espera microsegundos)
                    0,                      // Mínimo delay: se recomienda dejarlo igual para que la entrega de eventos sea rápida.
                    mSensorHandler
            );
            // Si el sensor es un contador de pasos, reinicia el valor inicial
            if (sensor.getSensorName().equals(CBBuffer.STRING_NUM_OF_STEPS)) {
                sensor.setInitialStepCount(-1); // Reinicia el valor inicial
            }
        }
    }

    private void unregisterDeviceSensors() {
        this.sensorManager.unregisterListener(this);
        for (CBSensorBuffer sensor : sensors) {
            sensor.resetBuffer();
        }
        mSensorThread.quitSafely();
    }

    /**
     * Se encarga de escribir los datos en archivos.
     */
    private void scheduleWriteFile() {
        this.timer = new Timer();
        WriteFileTask<CBSensorBuffer> writeFileTask = new WriteFileTask<>();
        writeFileTask.addSensors(this.sensors);
        timer.schedule(writeFileTask, WriteFileTask.DELAY_TIME_MS, WriteFileTask.PERIOD_TIME_MS);
    }

    /**
     * Procesa un único evento de sensor.
     * La lógica es la misma que en la versión anterior: encuentra el buffer correspondiente y lo actualiza.
     *
     * @param event El evento de sensor a procesar.
     */
    private void processSensorEvent(SensorEventCopy event) {
        int eventSensorType = event.sensorType;
        long eventTimestamp = event.timestamp;
        float[] eventValues = event.values;
        try {
            if (sensors.stream().noneMatch(s -> s.getSensor().getType() == eventSensorType)) {
                return;
            }
            CBSensorBuffer sensor = sensors.stream()
                    .filter(s -> s.getSensor().getType() == eventSensorType)
                    .findFirst()
                    .orElse(null);
            if (sensor == null) return;
            if (eventSensorType == Sensor.TYPE_STEP_COUNTER) {
                int stepCount = (int) eventValues[0];
                if (sensor.getInitialStepCount() == -1) {
                    sensor.setInitialStepCount(stepCount);
                }
                stepCount -= sensor.getInitialStepCount();
                sensor.append(new SensorEventData(eventTimestamp, stepCount));
            } else {
                sensor.append(new SensorEventData(eventTimestamp, eventValues[0], eventValues[1], eventValues[2]));
            }
        } catch (Exception e) {
            FileManager.writeToFile("error.txt", e.getMessage() + "\n");
        }
    }

    /*------------------------------- Cambios 3---------------------------------------------------*/

    /**
     * Clase interna para encapsular los datos de un evento de sensor.
     */
    private static class SensorEventCopy {
        final long timestamp; // En nanosegundos
        final float[] values;
        final int sensorType;

        SensorEventCopy(SensorEvent event) {
            this.timestamp = event.timestamp;
            this.values = event.values.clone();
            this.sensorType = event.sensor.getType();
        }
    }

    private final Runnable processBatchRunnable = new Runnable() {
        @Override
        public void run() {
            // Obtener el tiempo actual en milisegundos (tiempo real)
            long nowWallClockMs = System.currentTimeMillis();
            long nowSecond = nowWallClockMs / 1000;

            // Si es un nuevo segundo, procesar el anterior
            if (nowSecond != currentSecond) {
                if (currentSecond != -1) {
                    processSecond(currentSecond);
                }
                currentSecond = nowSecond;
            }

            // Acumular nuevos eventos
            SensorEventCopy event;
            while ((event = sensorEventQueue.poll()) != null) {
                // Calcular el segundo real del evento
                //long eventElapsedRealtimeMs = event.timestamp / 1_000_000;
                //long eventWallClockMs = eventElapsedRealtimeMs + bootOffset;
                //long eventSecond = eventWallClockMs / 1000;

                // Agrupar por tipo de sensor y segundo
                pendingEvents
                        .computeIfAbsent(event.sensorType, k -> new ArrayList<>())
                        .add(event);
            }

            // Reprogramar próxima ejecución en 100ms (detectar cambios de segundo rápido)
            if (state == SensorEventListenerState.RUNNING) {
                mSensorHandler.postDelayed(this, 100);
            }
        }

        private void processSecond(long targetSecond) {
            for (Map.Entry<Integer, List<SensorEventCopy>> entry : pendingEvents.entrySet()) {
                //int sensorType = entry.getKey();
                List<SensorEventCopy> events = entry.getValue();

                // Filtrar eventos del segundo objetivo
                List<SensorEventCopy> filtered = new ArrayList<>();
                for (SensorEventCopy event : events) {
                    long eventElapsedRealtimeMs = event.timestamp / 1_000_000;
                    long eventWallClockMs = eventElapsedRealtimeMs + bootOffset;
                    long eventSecond = eventWallClockMs / 1000;

                    if (eventSecond == targetSecond) {
                        filtered.add(event);
                    }
                }

                // Downsampling manual
                if (!filtered.isEmpty()) {
                    int desiredSamples = samplingRate;
                    List<SensorEventCopy> sampled = new ArrayList<>();

                    if (filtered.size() <= desiredSamples) {
                        sampled.addAll(filtered);
                    } else {
                        double step = (double) filtered.size() / desiredSamples;
                        for (int i = 0; i < desiredSamples; i++) {
                            int index = (int) Math.round(i * step);
                            index = Math.min(index, filtered.size() - 1);
                            sampled.add(filtered.get(index));
                        }
                    }

                    // Escribir muestras al buffer
                    for (SensorEventCopy event : sampled) {
                        processSensorEvent(event);
                    }
                }
            }

            // Limpiar eventos procesados
            pendingEvents.clear();
        }
    };
    /*------------------------------- Cambios 3---------------------------------------------------*/
}
